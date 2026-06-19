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

public class AuditLogDao {
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
