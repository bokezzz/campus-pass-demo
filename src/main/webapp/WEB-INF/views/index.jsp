<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
    <title>校园通行码预约管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="phone-shell">
    <div class="hero">
        <h1>校园通行码预约</h1>
        <p>支持社会公众进校预约和公务来访预约，审核通过后生成通行码。</p>
    </div>
    <div class="grid">
        <a class="card" href="${pageContext.request.contextPath}/reserve">
            <h2>我要预约</h2>
            <p class="muted">填写校区、进校时间、身份信息和来访事由。</p>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/my-reservations">
            <h2>我的预约</h2>
            <p class="muted">通过姓名、身份证号和手机号查询历史预约。</p>
        </a>
    </div>
    <p><a href="${pageContext.request.contextPath}/admin/login">后台管理入口</a></p>
</div>
</body>
</html>
