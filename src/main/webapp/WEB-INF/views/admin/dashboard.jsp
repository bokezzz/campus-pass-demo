<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.*" %>
<%
    AdminUser admin = (AdminUser) session.getAttribute("admin");
    List<StatisticRow> publicStats = (List<StatisticRow>) request.getAttribute("publicStats");
    List<StatisticRow> officialStats = (List<StatisticRow>) request.getAttribute("officialStats");
%>
<!doctype html>
<html>
<head>
    <title>后台首页</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<jsp:include page="nav.jsp"/>
<div class="shell">
    <div class="hero">
        <h1>校园通行码预约管理系统</h1>
        <p>当前用户：<%=admin.getName()%>（<%=admin.getRole().getLabel()%>）</p>
    </div>
    <div class="grid">
        <div class="panel">
            <h2>社会公众预约按校区统计</h2>
            <table><tr><th>校区</th><th>次数</th><th>人次</th></tr>
                <% for (StatisticRow row : publicStats) { %>
                <tr><td><%=row.getLabel()%></td><td><%=row.getReservations()%></td><td><%=row.getPeople()%></td></tr>
                <% } %>
            </table>
        </div>
        <div class="panel">
            <h2>公务预约按部门统计</h2>
            <table><tr><th>部门</th><th>次数</th><th>人次</th></tr>
                <% for (StatisticRow row : officialStats) { %>
                <tr><td><%=row.getLabel()%></td><td><%=row.getReservations()%></td><td><%=row.getPeople()%></td></tr>
                <% } %>
            </table>
        </div>
    </div>
</div>
</body>
</html>
