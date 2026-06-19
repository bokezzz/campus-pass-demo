package org.example.demo.model;

public enum AdminRole {
    SYSTEM_ADMIN("系统管理员"),
    SCHOOL_ADMIN("学校管理员"),
    DEPARTMENT_ADMIN("部门管理员"),
    AUDIT_ADMIN("审计管理员");

    private final String label;

    AdminRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
