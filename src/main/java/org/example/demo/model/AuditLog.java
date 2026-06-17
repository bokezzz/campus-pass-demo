package org.example.demo.model;

import java.time.LocalDateTime;

/**
 * 审计日志实体类，对应 audit_logs 表。
 *
 * <p>记录操作人、操作类型、详情、IP 和操作时间，用于安全审计页面展示。</p>
 */
public class AuditLog {
    private Long id;
    private String actor;
    private String action;
    private String detail;
    private String ipAddress;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
