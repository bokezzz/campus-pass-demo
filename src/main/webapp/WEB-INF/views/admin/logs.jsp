<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.AuditLog" %>
<%
    List<AuditLog> logs = (List<AuditLog>) request.getAttribute("logs");
%>
<!doctype html>
<html>
<head><title>审计日志</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css"></head>
<body>
<jsp:include page="nav.jsp"/>
<div class="shell">
    <div class="panel">
        <h1>审计日志</h1>
        <table>
            <tr><th>时间</th><th>用户</th><th>操作</th><th>详情</th><th>IP</th></tr>
            <% for (AuditLog log : logs) { %>
            <tr><td><%=log.getCreatedAt()%></td><td><%=log.getActor()%></td><td><%=log.getAction()%></td><td><%=log.getDetail()%></td><td><%=log.getIpAddress()%></td></tr>
            <% } %>
        </table>
    </div>
</div>
</body>
</html>
