<%@ page contentType="text/html;charset=UTF-8" import="org.example.demo.model.AdminUser,org.example.demo.model.ReservationType,org.example.demo.util.AdminAccessPolicy" %>
<%
    AdminUser navAdmin = (AdminUser) session.getAttribute("admin");
%>
<div class="topbar">
    <a href="${pageContext.request.contextPath}/admin/dashboard">首页</a>
    <% if (navAdmin != null && AdminAccessPolicy.canAccess("/admin/departments", null, navAdmin.getRole(), navAdmin.isPublicReservationPermission())) { %>
    <a href="${pageContext.request.contextPath}/admin/departments">部门管理</a>
    <% } %>
    <% if (navAdmin != null && AdminAccessPolicy.canAccess("/admin/users", null, navAdmin.getRole(), navAdmin.isPublicReservationPermission())) { %>
    <a href="${pageContext.request.contextPath}/admin/users">管理员管理</a>
    <% } %>
    <% if (navAdmin != null && AdminAccessPolicy.canManagePublicReservations(navAdmin.getRole(), navAdmin.isPublicReservationPermission())) { %>
    <a href="${pageContext.request.contextPath}/admin/reservations?type=PUBLIC">社会公众预约</a>
    <% } %>
    <% if (navAdmin != null && AdminAccessPolicy.canAccess("/admin/reservations", ReservationType.OFFICIAL, navAdmin.getRole(), navAdmin.isPublicReservationPermission())) { %>
    <a href="${pageContext.request.contextPath}/admin/reservations?type=OFFICIAL">公务预约</a>
    <% } %>
    <% if (navAdmin != null && AdminAccessPolicy.canAccess("/admin/logs", null, navAdmin.getRole(), navAdmin.isPublicReservationPermission())) { %>
    <a href="${pageContext.request.contextPath}/admin/logs">审计日志</a>
    <% } %>
    <span style="float:right"><%=navAdmin == null ? "" : navAdmin.getLoginName()%> <a href="${pageContext.request.contextPath}/admin/logout">退出</a></span>
</div>
