package org.example.demo.model;

import java.time.LocalDateTime;

/**
 * 后台管理员实体类，对应 admin_users 表。
 *
 * <p>字段包括登录名、SM3 密码摘要、角色、所属部门、手机号、
 * 社会公众预约授权，以及登录失败次数和锁定时间。</p>
 */
public class AdminUser {
    private Long id;
    private String name;
    private String loginName;
    private String passwordHash;
    private AdminRole role;
    private Long departmentId;
    private String departmentName;
    private String phone;
    private boolean publicReservationPermission;
    private int failedAttempts;
    private LocalDateTime lockedUntil;
    private LocalDateTime passwordChangedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPublicReservationPermission() {
        return publicReservationPermission;
    }

    public void setPublicReservationPermission(boolean publicReservationPermission) {
        this.publicReservationPermission = publicReservationPermission;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }
}
