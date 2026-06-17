package org.example.demo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 数据库工具类，负责整个系统的 JDBC 连接和首次启动时的表结构初始化。
 *
 * <p>答辩时可以说明：本项目采用 DAO + JDBC 的方式访问 MySQL，
 * 所有 DAO 都通过 {@link #getConnection()} 获取连接，避免在 Servlet
 * 中直接写数据库代码，从而符合 MVC 分层思想。</p>
 */
public final class DbUtil {
    /**
     * 默认连接本机 MySQL 的 campus_pass_demo 数据库。
     *
     * <p>createDatabaseIfNotExist=true 表示数据库不存在时自动创建，
     * 这样组员或老师第一次运行项目时不必手动建库。</p>
     *
     * <p>也可以通过 JVM 参数覆盖，例如：
     * -Dcampus.db.url=... -Dcampus.db.user=... -Dcampus.db.password=...</p>
     */
    private static final String URL = System.getProperty("campus.db.url",
            "jdbc:mysql://localhost:3306/campus_pass_demo?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USER = readConfig("campus.db.user", "CAMPUS_DB_USER", "root");
    private static final String PASSWORD = readConfig("campus.db.password", "CAMPUS_DB_PASSWORD", "");

    /**
     * 防止同一个 Web 应用生命周期内重复执行建表和种子数据初始化。
     * volatile 保证不同请求线程看到的 initialized 值是一致的。
     */
    private static volatile boolean initialized;

    private DbUtil() {
    }

    private static String readConfig(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        initialize();
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 初始化数据库结构。
     *
     * <p>课程设计演示时，经常会换电脑或重新部署，所以这里使用
     * create table if not exists 保证项目启动后自动具备基本数据表。</p>
     */
    public static synchronized void initialize() throws SQLException {
        if (initialized) {
            return;
        }
        try {
            // 显式加载 MySQL 驱动，避免某些 Tomcat 部署环境下 DriverManager 找不到驱动。
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            // 部门表：公务预约需要选择访问部门，后台也需要维护部门信息。
            statement.execute("""
                    create table if not exists departments (
                        id bigint primary key auto_increment,
                        code varchar(30) not null unique,
                        type varchar(30) not null,
                        name varchar(100) not null
                    )
                    """);
            // 管理员表：保存学校管理员、部门管理员、系统管理员、审计管理员等后台账号。
            // 密码字段只保存 SM3 摘要，不保存明文密码。
            statement.execute("""
                    create table if not exists admin_users (
                        id bigint primary key auto_increment,
                        name varchar(50) not null,
                        login_name varchar(50) not null unique,
                        password_hash varchar(128) not null,
                        role varchar(30) not null,
                        department_id bigint,
                        phone varchar(30),
                        public_reservation_permission boolean default false,
                        failed_attempts int default 0,
                        locked_until timestamp,
                        password_changed_at timestamp,
                        foreign key (department_id) references departments(id)
                    )
                    """);
            // 预约表：保存社会公众预约和公务预约的共同字段。
            // type 区分预约类型，status 表示待审核、已通过或未通过。
            statement.execute("""
                    create table if not exists reservations (
                        id bigint primary key auto_increment,
                        type varchar(20) not null,
                        status varchar(20) not null,
                        apply_time timestamp not null,
                        campus varchar(50) not null,
                        visit_time timestamp not null,
                        organization varchar(120) not null,
                        visitor_name varchar(50) not null,
                        identity_no varchar(32) not null,
                        phone varchar(30) not null,
                        traffic_type varchar(30) not null,
                        car_no varchar(30),
                        companions text,
                        visit_department_id bigint,
                        host_name varchar(50),
                        reason varchar(500),
                        review_comment varchar(500),
                        review_time timestamp,
                        pass_code varchar(50) not null,
                        foreign key (visit_department_id) references departments(id)
                    )
                    """);
            // 审计日志表：记录登录、查看通行码、查询预约、审核等关键操作。
            // hmac 字段用 SM3 做完整性摘要，体现任务书中的安全审计要求。
            statement.execute("""
                    create table if not exists audit_logs (
                        id bigint primary key auto_increment,
                        actor varchar(80) not null,
                        action varchar(80) not null,
                        detail varchar(800),
                        ip_address varchar(80),
                        created_at timestamp not null,
                        hmac varchar(128)
                    )
                    """);
        }
        seedData();
        initialized = true;
    }

    /**
     * 写入演示用的基础数据。
     *
     * <p>只有当表为空时才插入，避免每次启动都重复生成部门或管理员。</p>
     */
    private static void seedData() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (count(connection, "departments") == 0) {
                insertDepartment(connection, "D001", "学院", "计算机科学与技术学院");
                insertDepartment(connection, "D002", "行政部门", "保卫处");
                insertDepartment(connection, "D003", "直属部门", "校友办公室");
            }
            if (count(connection, "admin_users") == 0) {
                Long cs = findDepartmentId(connection, "D001");
                insertAdmin(connection, "系统管理员", "sysadmin", "System@123", "SYSTEM_ADMIN", null, "13800000001", true);
                insertAdmin(connection, "学校管理员", "school", "School@123", "SCHOOL_ADMIN", null, "13800000002", true);
                insertAdmin(connection, "学院管理员", "dept", "Dept@1234", "DEPARTMENT_ADMIN", cs, "13800000003", false);
                insertAdmin(connection, "审计管理员", "audit", "Audit@123", "AUDIT_ADMIN", null, "13800000004", false);
            }
        }
    }

    private static int count(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select count(*) from " + table)) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private static void insertDepartment(Connection connection, String code, String type, String name) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into departments(code,type,name) values(?,?,?)")) {
            statement.setString(1, code);
            statement.setString(2, type);
            statement.setString(3, name);
            statement.executeUpdate();
        }
    }

    private static Long findDepartmentId(Connection connection, String code) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select id from departments where code=?")) {
            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong("id") : null;
            }
        }
    }

    private static void insertAdmin(Connection connection, String name, String loginName, String password, String role, Long departmentId, String phone, boolean publicPermission) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
                insert into admin_users(name,login_name,password_hash,role,department_id,phone,public_reservation_permission,password_changed_at)
                values(?,?,?,?,?,?,?,?)
                """)) {
            statement.setString(1, name);
            statement.setString(2, loginName);
            statement.setString(3, SecurityUtil.sm3(password));
            statement.setString(4, role);
            if (departmentId == null) {
                statement.setObject(5, null);
            } else {
                statement.setLong(5, departmentId);
            }
            statement.setString(6, phone);
            statement.setBoolean(7, publicPermission);
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            // 管理员密码在入库前使用 SM3 摘要，满足任务书“密码用国密 SM3 加密存储”的要求。
            statement.executeUpdate();
        }
    }
}
