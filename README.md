# 校园通行码预约管理系统

这是 Web 应用开发课程设计第一题的实现，采用 Servlet + JSP + JavaBean + JDBC + DAO 的 MVC 结构。

## 功能

- 手机端：我要预约、我的预约、通行码展示。
- 社会公众预约：提交后自动审核通过。
- 公务预约：提交后待部门或学校管理员审核。
- 后台端：管理员登录、管理员管理、部门管理、社会公众预约管理、公务预约查询审核、统计、审计日志。
- 安全要求：SM3 密码摘要、密码复杂度校验、登录失败锁定、30 分钟会话超时、敏感信息脱敏显示、审计日志和 HMAC-SM3 摘要。

## 运行

开发运行：

```powershell
.\mvnw.cmd jetty:run
```

访问：

- 手机端：http://localhost:8080/demo/
- 后台端：http://localhost:8080/demo/admin/login

打包部署：

```powershell
.\mvnw.cmd clean package
```

生成的 WAR 文件位于：

```text
target/demo-1.0-SNAPSHOT.war
```

## 演示账号

| 角色 | 登录名 | 密码 |
| --- | --- | --- |
| 系统管理员 | sysadmin | System@123 |
| 学校管理员 | school | School@123 |
| 部门管理员 | dept | Dept@1234 |
| 审计管理员 | audit | Audit@123 |

默认使用本机 MySQL 数据库，数据库名为 `campus_pass_demo`，首次连接会自动创建。代码仓库不保存个人数据库密码；每位组员按自己电脑上的 MySQL 账号配置即可。可通过 JVM 参数覆盖：

```text
-Dcampus.db.url=jdbc:mysql://localhost:3306/campus_pass_demo?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
-Dcampus.db.user=root
-Dcampus.db.password=自己的MySQL密码
```

也可以使用环境变量：

```text
CAMPUS_DB_USER=root
CAMPUS_DB_PASSWORD=自己的MySQL密码
```
