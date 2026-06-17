<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.*" %>
<%
    List<AdminUser> users = (List<AdminUser>) request.getAttribute("users");
    List<Department> departments = (List<Department>) request.getAttribute("departments");
    AdminRole[] roles = (AdminRole[]) request.getAttribute("roles");
    AdminUser edit = (AdminUser) request.getAttribute("editUser");
%>
<!doctype html>
<html>
<head><title>管理员管理</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css"></head>
<body>
<jsp:include page="nav.jsp"/>
<div class="shell">
    <div class="panel">
        <h1>管理员管理</h1>
        <% if (request.getAttribute("error") != null) { %><p class="error"><%=request.getAttribute("error")%></p><% } %>
        <form method="post">
            <input type="hidden" name="id" value="<%=edit == null ? "" : edit.getId()%>">
            <div class="grid">
                <div><label>姓名</label><input name="name" required value="<%=edit == null ? "" : edit.getName()%>"></div>
                <div><label>登录名</label><input name="loginName" required value="<%=edit == null ? "" : edit.getLoginName()%>"></div>
                <div><label>密码</label><input name="password" type="password" placeholder="新增必填，修改可留空"></div>
                <div><label>角色</label><select name="role"><% for (AdminRole role : roles) { %><option value="<%=role.name()%>"><%=role.getLabel()%></option><% } %></select></div>
                <div><label>所在部门</label><select name="departmentId"><option value="">无</option><% for (Department d : departments) { %><option value="<%=d.getId()%>"><%=d.getName()%></option><% } %></select></div>
                <div><label>联系电话</label><input name="phone" value="<%=edit == null ? "" : edit.getPhone()%>"></div>
            </div>
            <label><input style="width:auto" type="checkbox" name="publicReservationPermission" <%=edit != null && edit.isPublicReservationPermission() ? "checked" : ""%>> 授权社会公众预约管理</label>
            <p><button type="submit">保存管理员</button></p>
        </form>
    </div>
    <div class="panel">
        <table>
            <tr><th>姓名</th><th>登录名</th><th>角色</th><th>部门</th><th>联系电话</th><th>操作</th></tr>
            <% for (AdminUser u : users) { %>
            <tr>
                <td><%=u.getName()%></td><td><%=u.getLoginName()%></td><td><%=u.getRole().getLabel()%></td><td><%=u.getDepartmentName() == null ? "" : u.getDepartmentName()%></td><td><%=u.getPhone()%></td>
                <td><a href="?edit=<%=u.getId()%>">修改</a> <form method="post" style="display:inline"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="<%=u.getId()%>"><button class="danger" type="submit">删除</button></form></td>
            </tr>
            <% } %>
        </table>
    </div>
</div>
</body>
</html>
