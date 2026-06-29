package com.civicdesk.module.auditlog.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import com.civicdesk.module.auditlog.entity.AuditLog;


public final class AuditLogSpecifications {

    private AuditLogSpecifications() {
    }

    public static Specification<AuditLog> hasUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<AuditLog> hasAction(String action) {
        return (root, query, cb) -> cb.equal(root.get("action"), action.trim().toUpperCase());
    }

    public static Specification<AuditLog> hasModule(String module) {
        return (root, query, cb) -> cb.equal(root.get("module"), module.trim().toUpperCase());
    }
}
