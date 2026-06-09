# CivicDesk — Module 2.1: Identity & Access Management

Foundation module for CivicDesk. Provides citizen/staff authentication, JWT issuance,
role-based access control, user administration, and audit logging.

## Stack

- Spring Boot 3.4.x + Spring Security 6
- MySQL 8 (runtime) · H2 (tests)
- JWT HS256 (stateless) via jjwt 0.12.3
- BCrypt password hashing (cost 10)
- UUID v4 primary keys (`CHAR(36)`)

> **Note on the framework version:** the project was scaffolded with Spring Boot
> `4.0.6`, but the assignment spec and all provided code target **Spring Boot 3.x /
> Spring Security 6**. `pom.xml` is therefore pinned to `3.4.1` so the documented
> APIs (lambda security DSL, `@EnableMethodSecurity`, jjwt 0.12.3) compile as written.

## Running

```bash
# The civicdesk database is auto-created on first start
# (JDBC URL has ?createDatabaseIfNotExist=true; ddl-auto=update builds the tables).
# Set spring.datasource.username / password in src/main/resources/application.properties, then:
./mvnw spring-boot:run                              # default profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev   # verbose SQL
```

App starts on `http://localhost:8081`.

- **Swagger UI:** http://localhost:8081/swagger-ui.html (use *Authorize* to paste a JWT)
- A default **ADMIN** is seeded on first startup by `DataSeeder` from the
  `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `ADMIN_NAME` / `ADMIN_PHONE` env vars
  (local defaults: `admin@civicdesk.gov` / `Admin@12345`).

### Profiles
- `dev` — `ddl-auto=update`, SQL logging on.
- `prod` — `ddl-auto=validate`, no SQL logging; DB + JWT secret come from env vars.

### Docker
```bash
docker build -t civicdesk-iam .
docker run -p 8081:8081 --env-file .env civicdesk-iam
```

## Tests

```bash
./mvnw test
```

Tests run against in-memory H2 using the `test` profile (`application-test.properties`).

## Project layout

```
com.civicdesk
├── CivicDeskApplication.java
├── config/        SecurityConfig · JwtConfig · CorsConfig · OpenApiConfig · DataSeeder
├── common/
│   ├── exception/  GlobalExceptionHandler + domain exceptions (401/403/404/409/400/423)
│   ├── response/   ApiResponse (unified, with statusCode) · ErrorResponse · PageResponse
│   └── util/       JwtUtil · NationalIdUtil · SecurityContextUtil
└── module/iam/
    ├── controller/ AuthController · UserController · AuditLogController
    ├── service/    Auth / User / Audit services (interface + impl)
    ├── repository/  UserRepository · AuditLogRepository
    ├── entity/     User · AuditLog
    ├── dto/
    │   ├── request/   Register · CitizenLogin · StaffLogin · CreateUser · UpdateUserStatus
    │   └── response/  AuthResponse · UserResponse · AuditLogResponse
    ├── enums/      Role · UserStatus · AuditAction · AuditModule  (share with team Day 1)
    └── security/   JwtAuthFilter
```

Teammates' modules (`citizen/`, `servicerequest/`, `permit/`, `grievance/`, `analytics/`)
live as siblings under `module/`.

## Endpoints (10)

| Method | Path                          | Access                              |
|--------|-------------------------------|-------------------------------------|
| POST   | `/api/v1/auth/register`       | Public — citizen self-registration  |
| POST   | `/api/v1/auth/citizen/login`  | Public — CITIZEN only (else 403)    |
| POST   | `/api/v1/auth/staff/login`    | Public — staff only (CITIZEN → 403); not-yet-set password → 403; suspended → 423 |
| POST   | `/api/v1/auth/set-password`   | Public — first-time password setup (one-time per account) |
| POST   | `/api/v1/auth/logout`         | Any authenticated user              |
| GET    | `/api/v1/departments`         | ADMIN · DEPT_SUPERVISOR             |
| GET    | `/api/v1/users/me`            | Any authenticated user              |
| POST   | `/api/v1/users`               | ADMIN → DEPT_SUPERVISOR; SUPERVISOR → field staff |
| GET    | `/api/v1/users`               | ADMIN (all) · DEPT_SUPERVISOR (own dept) |
| PUT    | `/api/v1/users/{id}/status`   | ADMIN only                          |
| GET    | `/api/v1/audit-logs`          | ADMIN · COMPLIANCE_OFFICER          |

All responses use the standard envelope:
`{ "success", "statusCode", "message", "data", "timestamp" }`.

## Staff onboarding (set-password on first login)

Admin/supervisor-created accounts are created **without a password**
(`is_password_set = false`). The owner activates the account themselves:

```
Admin creates user (no password)         → is_password_set = false
Staff POST /auth/staff/login             → 403 "Please set your password before logging in"
Staff POST /auth/set-password            → validates min length, BCrypt-hashes,
   { email, newPassword }                   sets is_password_set = true  (200)
Staff POST /auth/staff/login again       → 200 + JWT
```

`set-password` works **once** per account (afterwards → 403, use a reset flow).
Citizens (self-register) and the seeded admin already have `is_password_set = true`.

## Departments

`DataSeeder` seeds six departments on startup: **Infrastructure, Public Health,
Licensing & Compliance, Citizen Services, Administration, Compliance & Audit**.
When an ADMIN creates a `DEPT_SUPERVISOR`, the `departmentId` is **validated against
the departments table** (unknown id → 400) and the department's
`department_supervisor_id` is set to the new supervisor (one-to-one). Use
`GET /api/v1/departments` to fetch valid department ids.

## Shared with teammates

Hand `Role.java` and `JwtUtil.java` to the other module owners before they start —
the role strings and JWT claim names (`userId`, `role`, `email`) are the cross-module
contract.
