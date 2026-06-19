<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.Department" %>
<%
    List<Department> departments = (List<Department>) request.getAttribute("departments");
    Department edit = (Department) request.getAttribute("editDepartment");
%>
<!doctype html>
<html>
<head><title>部门管理</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css"></head>
<body>
<jsp:include page="nav.jsp"/>
<div class="shell">
    <div class="panel">
        <h1>部门管理</h1>
        <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%=request.getAttribute("error")%></p>
        <% } %>
        <form method="post">
            <input type="hidden" name="id" value="<%=edit == null ? "" : edit.getId()%>">
            <div class="grid">
                <div><label>部门编号</label><input name="code" required value="<%=edit == null ? "" : edit.getCode()%>"></div>
                <div><label>部门类型</label><select name="type"><option>行政部门</option><option>直属部门</option><option>学院</option></select></div>
                <div><label>部门名称</label><input name="name" required value="<%=edit == null ? "" : edit.getName()%>"></div>
            </div>
            <p><button type="submit">保存部门</button></p>
        </form>
    </div>
    <div class="panel">
        <table>
            <tr><th>编号</th><th>类型</th><th>名称</th><th>操作</th></tr>
            <% for (Department d : departments) { %>
            <tr>
                <td><%=d.getCode()%></td><td><%=d.getType()%></td><td><%=d.getName()%></td>
                <td>
                    <a href="?edit=<%=d.getId()%>">修改</a>
                    <form method="post" style="display:inline"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="<%=d.getId()%>"><button class="danger" type="submit">删除</button></form>
                </td>
            </tr>
            <% } %>
        </table>
    </div>
</div>
</body>
</html>
