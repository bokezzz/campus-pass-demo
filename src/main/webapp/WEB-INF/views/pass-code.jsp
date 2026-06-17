<%@ page contentType="text/html;charset=UTF-8" import="org.example.demo.model.Reservation,org.example.demo.util.SecurityUtil" %>
<%
    Reservation r = (Reservation) request.getAttribute("reservation");
    boolean valid = Boolean.TRUE.equals(request.getAttribute("valid"));
%>
<!doctype html>
<html>
<head>
    <title>通行码</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="phone-shell">
    <% if (r == null) { %>
    <div class="panel"><p class="error">未找到预约记录。</p></div>
    <% } else { %>
    <div class="pass-card <%=valid ? "" : "invalid"%>">
        <h1><%=valid ? "有效通行码" : "无效通行码"%></h1>
        <img class="qr-img" src="${pageContext.request.contextPath}/qr-code?id=<%=r.getId()%>" alt="通行二维码">
        <h2><%=SecurityUtil.maskName(r.getVisitorName())%></h2>
        <p>身份证号：<%=SecurityUtil.maskIdentityNo(r.getIdentityNo())%></p>
        <p>手机号：<%=SecurityUtil.maskPhone(r.getPhone())%></p>
        <p>校区：<%=r.getCampus()%></p>
        <p>进校时间：<%=r.getVisitTime()%></p>
        <p>通行码编号：<%=r.getPassCode()%></p>
        <p>状态：<%=r.getStatus().getLabel()%></p>
    </div>
    <% } %>
    <p><a class="button secondary" href="${pageContext.request.contextPath}/">返回首页</a></p>
</div>
</body>
</html>
