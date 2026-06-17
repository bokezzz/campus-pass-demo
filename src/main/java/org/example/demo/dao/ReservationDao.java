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

/**
 * 预约数据访问对象。
 *
 * <p>DAO 层只负责和数据库交互，不直接处理页面跳转。
 * 这样 Servlet、Service、DAO 三层职责清晰，符合课程设计要求的 DAO + MVC 模式。</p>
 */
public class ReservationDao {
    /**
     * 保存一条新预约，并返回数据库自动生成的主键 ID。
     *
     * <p>返回 ID 后，Servlet 可以跳转到 /pass-code?id=... 显示刚生成的通行码。</p>
     */
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

    /**
     * 按预约 ID 查询单条记录。
     *
     * <p>通行码页面和二维码图片接口都需要通过 ID 找到对应预约记录。</p>
     */
    public Reservation findById(long id) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(baseSql() + " where r.id=?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }
        }
    }

    /**
     * 手机端“我的预约”查询。
     *
     * <p>任务书要求输入本人姓名、身份证号、手机号查询历史预约记录，
     * 所以这里三个条件都必须匹配，避免别人只知道姓名就查到隐私信息。</p>
     */
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

    /**
     * 后台预约管理的组合查询。
     *
     * <p>keyword、campus、status、visitDate 都是可选条件。
     * 这里用 StringBuilder 动态拼接 SQL 条件，同时用 PreparedStatement
     * 绑定参数，既灵活又能防止 SQL 注入。</p>
     *
     * <p>权限控制：如果当前用户是部门管理员，并且查询公务预约，
     * 则强制增加 visit_department_id 条件，只能看到本部门的公务预约。</p>
     */
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

    /**
     * 公务预约审核。
     *
     * <p>审核时只更新状态、审核意见和审核时间；预约申请原始信息保持不变，
     * 方便后续审计和追溯。</p>
     */
    public void review(long id, ReservationStatus status, String comment) throws SQLException {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("update reservations set status=?, review_comment=?, review_time=current_timestamp where id=?")) {
            statement.setString(1, status.name());
            statement.setString(2, comment);
            statement.setLong(3, id);
            statement.executeUpdate();
        }
    }

    /**
     * 后台统计查询。
     *
     * <p>根据参数 dimension 支持按申请月度、预约月度、预约校区、公务访问部门统计。
     * 统计结果返回 StatisticRow，页面只负责显示表格。</p>
     */
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

    /**
     * 预约查询的基础 SQL。
     *
     * <p>left join departments 是为了在查询预约时同时显示公务访问部门名称。</p>
     */
    private String baseSql() {
        return """
                select r.*, d.name visit_department_name from reservations r
                left join departments d on r.visit_department_id=d.id
                """;
    }

    /**
     * 将 Reservation 对象中的字段绑定到 PreparedStatement。
     *
     * <p>新增预约字段比较多，单独抽出 fill 方法能减少 save 方法长度，
     * 也方便检查字段顺序是否和 SQL 占位符一致。</p>
     */
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

    /**
     * 将 ResultSet 当前行转换为 Reservation JavaBean。
     *
     * <p>这是典型的 DAO 映射逻辑：数据库表字段转换成 Java 对象，
     * 之后 Servlet/JSP 就不需要直接依赖 ResultSet。</p>
     */
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
