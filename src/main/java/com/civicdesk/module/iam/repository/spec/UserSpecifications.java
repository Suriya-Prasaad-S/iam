package com.civicdesk.module.iam.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import com.civicdesk.module.iam.entity.User;


public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<User> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<User> inDepartment(String departmentId) {
        return (root, query, cb) -> cb.equal(root.get("departmentId"), departmentId);
    }
}
