package org.example.demo.dao;

import org.example.demo.model.AuditLog;
import org.example.demo.util.DbUtil;
import org.example.demo.util.SecurityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 审计日志 DAO。
 *
 * <p>任务书要求保存登录、查看预约信息等重要操作日志。
 * 本类集中负责日志写入和日志查询，审计管理员页面读取的就是这里的数据。</p>
 */
public class AuditLogDao {
    /**
     * 写入一条审计日志。
     *
     * <p>日志写入失败时不打断主业务流程，所以这里捕获 SQLException。
     * hmac 字段用 SM3 对关键字段做摘要，演示“日志完整性保护”的思路。</p>
     */
    public void log(String actor, String action, String detail, String ipAddress) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     insert into audit_logs(actor,action,detail,ip_address,created_at,hmac)
                     values(?,?,?,?,?,?)
                     """)) {
            LocalDateTime now = LocalDateTime.now();
            String safeActor = actor == null || actor.isBlank() ? "anonymous" : actor;
            statement.setString(1, safeActor);
            statement.setString(2, action);
            statement.setString(3, detail);
            statement.setString(4, ipAddress);
            statement.setTimestamp(5, Timestamp.valueOf(now));
            statement.setString(6, SecurityUtil.sm3(safeActor + action + detail + now));
            statement.executeUpdate();
        } catch (SQLException ignored) {
            // Audit logging must not break the user workflow.
        }
    }

    /** 查询最近 200 条审计日志，供后台审计页面展示。 */
    public List<AuditLog> findAll() throws SQLException {
        List<AuditLog> logs = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from audit_logs order by id desc limit 200");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                AuditLog log = new AuditLog();
                log.setId(resultSet.getLong("id"));
                log.setActor(resultSet.getString("actor"));
                log.setAction(resultSet.getString("action"));
                log.setDetail(resultSet.getString("detail"));
                log.setIpAddress(resultSet.getString("ip_address"));
                log.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                logs.add(log);
            }
        }
        return logs;
    }
}
