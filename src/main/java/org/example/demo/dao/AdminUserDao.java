package org.example.demo.dao;

import org.example.demo.model.AdminRole;
import org.example.demo.model.AdminUser;
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

public class AdminUserDao {
    public AdminUser findByLoginName(String loginName) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     select u.*, d.name department_name from admin_users u
                     left join departments d on u.department_id=d.id
                     where u.login_name=?
                     """)) {
            statement.setString(1, loginName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    public AdminUser findById(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     select u.*, d.name department_name from admin_users u
                     left join departments d on u.department_id=d.id
                     where u.id=?
                     """)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    public List<AdminUser> findAll() throws SQLException {
        List<AdminUser> users = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     select u.*, d.name department_name from admin_users u
                     left join departments d on u.department_id=d.id
                     order by u.id
                     """);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
        }
        return users;
    }

    public void save(AdminUser user, String plainPassword) throws SQLException {
        if (user.getId() == null) {
            try (Connection connection = DbUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement("""
                         insert into admin_users(name,login_name,password_hash,role,department_id,phone,public_reservation_permission,password_changed_at)
                         values(?,?,?,?,?,?,?,?)
                         """)) {
                fillForSave(statement, user, plainPassword);
                statement.executeUpdate();
            }
            return;
        }
        String passwordPart = plainPassword == null || plainPassword.isBlank() ? "" : ", password_hash=?, password_changed_at=?";
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     update admin_users set name=?, login_name=?, role=?, department_id=?, phone=?, public_reservation_permission=?
                     """ + passwordPart + " where id=?")) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLoginName());
            statement.setString(3, user.getRole().name());
            if (user.getDepartmentId() == null) {
                statement.setObject(4, null);
            } else {
                statement.setLong(4, user.getDepartmentId());
            }
            statement.setString(5, user.getPhone());
            statement.setBoolean(6, user.isPublicReservationPermission());
            int index = 7;
            if (!passwordPart.isEmpty()) {
                // 管理员密码不保存明文，只保存国密 SM3 摘要。
                statement.setString(index++, SecurityUtil.sm3(plainPassword));
                statement.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            }
            statement.setLong(index, user.getId());
            statement.executeUpdate();
        }
    }

    public void recordLoginFailure(AdminUser user) throws SQLException {
        int attempts = user.getFailedAttempts() + 1;
        LocalDateTime lockedUntil = attempts >= 5 ? LocalDateTime.now().plusMinutes(30) : null;
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("update admin_users set failed_attempts=?, locked_until=? where id=?")) {
            statement.setInt(1, attempts);
            if (lockedUntil == null) {
                statement.setObject(2, null);
            } else {
                statement.setTimestamp(2, Timestamp.valueOf(lockedUntil));
            }
            statement.setLong(3, user.getId());
            statement.executeUpdate();
        }
    }

    public void clearLoginFailure(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("update admin_users set failed_attempts=0, locked_until=null where id=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from admin_users where id=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    private void fillForSave(PreparedStatement statement, AdminUser user, String plainPassword) throws SQLException {
        statement.setString(1, user.getName());
        statement.setString(2, user.getLoginName());
        statement.setString(3, SecurityUtil.sm3(plainPassword));
        statement.setString(4, user.getRole().name());
        if (user.getDepartmentId() == null) {
            statement.setObject(5, null);
        } else {
            statement.setLong(5, user.getDepartmentId());
        }
        statement.setString(6, user.getPhone());
        statement.setBoolean(7, user.isPublicReservationPermission());
        statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
    }

    private AdminUser map(ResultSet resultSet) throws SQLException {
        AdminUser user = new AdminUser();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setLoginName(resultSet.getString("login_name"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(AdminRole.valueOf(resultSet.getString("role")));
        long departmentId = resultSet.getLong("department_id");
        user.setDepartmentId(resultSet.wasNull() ? null : departmentId);
        user.setDepartmentName(resultSet.getString("department_name"));
        user.setPhone(resultSet.getString("phone"));
        user.setPublicReservationPermission(resultSet.getBoolean("public_reservation_permission"));
        user.setFailedAttempts(resultSet.getInt("failed_attempts"));
        Timestamp lockedUntil = resultSet.getTimestamp("locked_until");
        user.setLockedUntil(lockedUntil == null ? null : lockedUntil.toLocalDateTime());
        Timestamp passwordChangedAt = resultSet.getTimestamp("password_changed_at");
        user.setPasswordChangedAt(passwordChangedAt == null ? null : passwordChangedAt.toLocalDateTime());
        return user;
    }
}
