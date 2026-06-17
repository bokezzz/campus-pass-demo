package org.example.demo.model;

/**
 * 后台管理员角色枚举。
 *
 * <p>系统通过角色区分系统管理员、学校管理员、部门管理员和审计管理员，
 * 用于后台功能权限控制。</p>
 */
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
