<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.Reservation,org.example.demo.util.SecurityUtil" %>
<%
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
%>
<!doctype html>
<html>
<head>
    <title>我的预约</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="phone-shell">
    <div class="panel">
        <h1>我的预约</h1>
        <form method="post">
            <label>姓名</label><input name="visitorName" required>
            <label>身份证号</label><input name="identityNo" required>
            <label>手机号</label><input name="phone" required>
            <p><button type="submit">查询</button> <a class="button secondary" href="${pageContext.request.contextPath}/">返回</a></p>
        </form>
    </div>
    <% if (reservations != null) { %>
    <div class="panel">
        <h2>查询结果</h2>
        <table>
            <tr><th>申请日期</th><th>进校日期</th><th>校区</th><th>状态</th><th>操作</th></tr>
            <% for (Reservation r : reservations) { %>
            <tr>
                <td><%=r.getApplyTime().toLocalDate()%></td>
                <td><%=r.getVisitTime().toLocalDate()%></td>
                <td><%=r.getCampus()%></td>
                <td><%=r.getStatus().getLabel()%></td>
                <td><a href="${pageContext.request.contextPath}/pass-code?id=<%=r.getId()%>">查看</a></td>
            </tr>
            <% } %>
        </table>
    </div>
    <% } %>
</div>
</body>
</html>
