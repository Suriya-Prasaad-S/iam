package com.civicdesk.config;

import com.civicdesk.module.auditlog.enums.AuditAction;
import com.civicdesk.module.auditlog.enums.AuditModule;
import com.civicdesk.module.auditlog.service.AuditService;
import com.civicdesk.module.iam.entity.Department;
import com.civicdesk.module.iam.entity.User;
import com.civicdesk.module.iam.enums.Role;
import com.civicdesk.module.iam.enums.UserStatus;
import com.civicdesk.module.iam.repository.DepartmentRepository;
import com.civicdesk.module.iam.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.phone}")
    private String adminPhone;

    private static final List<String> DEPARTMENTS = List.of(
            "Infrastructure",
            "Public Health",
            "Licensing & Compliance",
            "Citizen Services",
            "Administration",
            "Compliance & Audit");

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public DataSeeder(UserRepository userRepository,
                      DepartmentRepository departmentRepository,
                      PasswordEncoder passwordEncoder,
                      AuditService auditService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    public void run(String... args) {
        seedDepartments();
        seedAdmin();
    }

    private void seedDepartments() {
        for (String name : DEPARTMENTS) {
            if (!departmentRepository.existsByName(name)) {
                Department dept = new Department(name);
                dept.setDepartmentId(nextDepartmentId());
                departmentRepository.save(dept);
                log.info("Seeded department '{}' as '{}'.", name, dept.getDepartmentId());
            }
        }
    }

    
    private String nextDepartmentId() {
        int max = departmentRepository.findAll().stream()
                .map(Department::getDepartmentId)
                .filter(id -> id != null && id.matches("DPT\\d+"))
                .mapToInt(id -> Integer.parseInt(id.substring(3)))
                .max()
                .orElse(0);
        return String.format("DPT%02d", max + 1);
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Default ADM '{}' already present â€” skipping seed.", adminEmail);
            return;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPhone(adminPhone);
        admin.setRole(Role.ADM.name());
        admin.setStatus(UserStatus.ACT.getLabel());
        admin.setDepartmentId(null);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setPasswordSet(true); 
        userRepository.save(admin);

        auditService.log(admin.getUserId(), AuditAction.SEED_ADMIN.name(), AuditModule.IAM.name(), "system");
        log.info("Seeded default ADM account '{}'.", adminEmail);
    }
}
