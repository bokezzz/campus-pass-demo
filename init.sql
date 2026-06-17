create database if not exists campus_pass_demo
    default character set utf8mb4
    default collate utf8mb4_unicode_ci;

use campus_pass_demo;

create table if not exists departments (
    id bigint primary key auto_increment,
    code varchar(30) not null unique,
    type varchar(30) not null,
    name varchar(100) not null
) engine=InnoDB default charset=utf8mb4;

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
    locked_until timestamp null,
    password_changed_at timestamp null,
    constraint fk_admin_department foreign key (department_id) references departments(id)
) engine=InnoDB default charset=utf8mb4;

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
    review_time timestamp null,
    pass_code varchar(50) not null,
    constraint fk_reservation_department foreign key (visit_department_id) references departments(id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists audit_logs (
    id bigint primary key auto_increment,
    actor varchar(80) not null,
    action varchar(80) not null,
    detail varchar(800),
    ip_address varchar(80),
    created_at timestamp not null,
    hmac varchar(128)
) engine=InnoDB default charset=utf8mb4;

insert ignore into departments(code, type, name) values
('D001', '学院', '计算机科学与技术学院'),
('D002', '行政部门', '保卫处'),
('D003', '直属部门', '校友办公室');

-- 演示管理员会在系统首次启动时自动写入，密码使用 SM3 摘要保存。
-- sysadmin/System@123, school/School@123, dept/Dept@1234, audit/Audit@123
