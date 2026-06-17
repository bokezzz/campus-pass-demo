<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
    <title>后台登录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="phone-shell">
    <div class="panel">
        <h1>管理端登录</h1>
        <% if (request.getAttribute("error") != null) { %><p class="error"><%=request.getAttribute("error")%></p><% } %>
        <form method="post">
            <label>登录名</label>
            <input name="loginName" required>
            <label>密码</label>
            <input name="password" type="password" required>
            <p><button type="submit">登录</button></p>
        </form>
        <p class="muted">演示账号：sysadmin/System@123，school/School@123，dept/Dept@1234，audit/Audit@123</p>
    </div>
</div>
</body>
</html>
