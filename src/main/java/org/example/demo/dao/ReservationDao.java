package org.example.demo.dao;

import org.example.demo.model.AdminRole;
import org.example.demo.model.AdminUser;
import org.example.demo.model.Reservation;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;
import org.example.demo.model.StatisticRow;
import org.example.demo.service.ReservationService;
import org.example.demo.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {
    public long save(Reservation reservation) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     insert into reservations(type,status,apply_time,campus,visit_time,organization,visitor_name,identity_no,phone,traffic_type,car_no,companions,visit_department_id,host_name,reason,review_comment,review_time,pass_code)
                     values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                     """, Statement.RETURN_GENERATED_KEYS)) {
            fill(statement, reservation);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public Reservation findById(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(baseSql() + " where r.id=?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    public List<Reservation> findByVisitor(String name, String identityNo, String phone) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(baseSql() + """
                     where r.visitor_name=? and r.identity_no=? and r.phone=?
                     order by r.apply_time desc
                     """)) {
            statement.setString(1, name);
            statement.setString(2, identityNo);
            statement.setString(3, phone);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(map(resultSet));
                }
            }
        }
        return reservations;
    }

    public List<Reservation> search(ReservationType type, AdminUser viewer, String keyword, String campus, String status, String visitDate) throws SQLException {
        StringBuilder sql = new StringBuilder(baseSql()).append(" where r.type=?");
        List<Object> params = new ArrayList<>();
        params.add(type.name());
        if (viewer.getRole() == AdminRole.DEPARTMENT_ADMIN && type == ReservationType.OFFICIAL) {
            sql.append(" and r.visit_department_id=?");
            params.add(viewer.getDepartmentId());
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" and (r.visitor_name like ? or r.identity_no like ? or r.organization like ? or r.host_name like ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (campus != null && !campus.isBlank()) {
            sql.append(" and r.campus=?");
            params.add(campus);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" and r.status=?");
            params.add(status);
        }
        if (visitDate != null && !visitDate.isBlank()) {
            sql.append(" and cast(r.visit_time as date)=?");
            params.add(LocalDate.parse(visitDate));
        }
        sql.append(" order by r.apply_time desc");
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reservations.add(map(resultSet));
                }
            }
        }
        return reservations;
    }

    public void review(long id, ReservationStatus status, String comment) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("update reservations set status=?, review_comment=?, review_time=current_timestamp where id=?")) {
            statement.setString(1, status.name());
            statement.setString(2, comment);
            statement.setLong(3, id);
            statement.executeUpdate();
        }
    }

    public List<StatisticRow> statistics(ReservationType type, String dimension, AdminUser viewer) throws SQLException {
        String expression = switch (dimension) {
            case "visitMonth" -> "date_format(r.visit_time, '%Y-%m')";
            case "campus" -> "r.campus";
            case "department" -> "coalesce(d.name, '无')";
            default -> "date_format(r.apply_time, '%Y-%m')";
        };
        StringBuilder sql = new StringBuilder("select " + expression + " label, count(*) total, sum(case when r.companions is null or trim(r.companions)='' then 1 else 2 end) people from reservations r left join departments d on r.visit_department_id=d.id where r.type=?");
        List<Object> params = new ArrayList<>();
        params.add(type.name());
        if (viewer.getRole() == AdminRole.DEPARTMENT_ADMIN && type == ReservationType.OFFICIAL) {
            sql.append(" and r.visit_department_id=?");
            params.add(viewer.getDepartmentId());
        }
        sql.append(" group by ").append(expression).append(" order by label desc");
        List<StatisticRow> rows = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new StatisticRow(resultSet.getString("label"), resultSet.getInt("total"), resultSet.getInt("people")));
                }
            }
        }
        return rows;
    }

    private String baseSql() {
        return """
                select r.*, d.name visit_department_name from reservations r
                left join departments d on r.visit_department_id=d.id
                """;
    }

    private void fill(PreparedStatement statement, Reservation reservation) throws SQLException {
        statement.setString(1, reservation.getType().name());
        statement.setString(2, reservation.getStatus().name());
        statement.setTimestamp(3, Timestamp.valueOf(reservation.getApplyTime()));
        statement.setString(4, reservation.getCampus());
        statement.setTimestamp(5, Timestamp.valueOf(reservation.getVisitTime()));
        statement.setString(6, reservation.getOrganization());
        statement.setString(7, reservation.getVisitorName());
        statement.setString(8, reservation.getIdentityNo());
        statement.setString(9, reservation.getPhone());
        statement.setString(10, reservation.getTrafficType());
        statement.setString(11, reservation.getCarNo());
        statement.setString(12, reservation.getCompanions());
        if (reservation.getVisitDepartmentId() == null) {
            statement.setObject(13, null);
        } else {
            statement.setLong(13, reservation.getVisitDepartmentId());
        }
        statement.setString(14, reservation.getHostName());
        statement.setString(15, reservation.getReason());
        statement.setString(16, reservation.getReviewComment());
        if (reservation.getReviewTime() == null) {
            statement.setObject(17, null);
        } else {
            statement.setTimestamp(17, Timestamp.valueOf(reservation.getReviewTime()));
        }
        statement.setString(18, reservation.getPassCode());
    }

    private Reservation map(ResultSet resultSet) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(resultSet.getLong("id"));
        reservation.setType(ReservationType.valueOf(resultSet.getString("type")));
        reservation.setStatus(ReservationStatus.valueOf(resultSet.getString("status")));
        reservation.setApplyTime(resultSet.getTimestamp("apply_time").toLocalDateTime());
        reservation.setCampus(resultSet.getString("campus"));
        reservation.setVisitTime(resultSet.getTimestamp("visit_time").toLocalDateTime());
        reservation.setOrganization(resultSet.getString("organization"));
        reservation.setVisitorName(resultSet.getString("visitor_name"));
        reservation.setIdentityNo(resultSet.getString("identity_no"));
        reservation.setPhone(resultSet.getString("phone"));
        reservation.setTrafficType(resultSet.getString("traffic_type"));
        reservation.setCarNo(resultSet.getString("car_no"));
        reservation.setCompanions(resultSet.getString("companions"));
        long departmentId = resultSet.getLong("visit_department_id");
        reservation.setVisitDepartmentId(resultSet.wasNull() ? null : departmentId);
        reservation.setVisitDepartmentName(resultSet.getString("visit_department_name"));
        reservation.setHostName(resultSet.getString("host_name"));
        reservation.setReason(resultSet.getString("reason"));
        reservation.setReviewComment(resultSet.getString("review_comment"));
        Timestamp reviewTime = resultSet.getTimestamp("review_time");
        reservation.setReviewTime(reviewTime == null ? null : reviewTime.toLocalDateTime());
        reservation.setPassCode(resultSet.getString("pass_code"));
        return reservation;
    }
}
