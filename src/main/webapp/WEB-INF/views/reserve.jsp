<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.Department" %>
<%
    List<Department> departments = (List<Department>) request.getAttribute("departments");
%>
<!doctype html>
<html>
<head>
    <title>我要预约</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="phone-shell">
    <div class="panel">
        <h1>我要预约</h1>
        <form method="post" action="${pageContext.request.contextPath}/reservation/create">
            <label>预约类型</label>
            <select name="type" id="type" onchange="toggleOfficial()">
                <option value="PUBLIC">社会公众预约</option>
                <option value="OFFICIAL">公务预约</option>
            </select>
            <label>预约校区</label>
            <select name="campus" required>
                <option>朝晖校区</option>
                <option>屏峰校区</option>
                <option>莫干山校区</option>
            </select>
            <label>预约进校时间</label>
            <input type="datetime-local" name="visitTime" required>
            <label>所在单位</label>
            <input name="organization" required>
            <label>姓名</label>
            <input name="visitorName" required>
            <label>身份证号</label>
            <input name="identityNo" required>
            <label>手机号</label>
            <input name="phone" required>
            <label>交通方式</label>
            <select name="trafficType">
                <option>步行</option>
                <option>地铁</option>
                <option>公交</option>
                <option>自驾</option>
            </select>
            <label>车牌号（可选）</label>
            <input name="carNo">
            <label>随行人员（每行：姓名 身份证号 手机号，可选）</label>
            <textarea name="companions"></textarea>
            <div id="officialFields" style="display:none">
                <label>公务访问部门</label>
                <select name="visitDepartmentId">
                    <% for (Department d : departments) { %>
                    <option value="<%=d.getId()%>"><%=d.getName()%></option>
                    <% } %>
                </select>
                <label>公务访问接待人</label>
                <input name="hostName">
                <label>来访事由</label>
                <textarea name="reason"></textarea>
            </div>
            <p><button type="submit">提交预约</button> <a class="button secondary" href="${pageContext.request.contextPath}/">返回</a></p>
        </form>
    </div>
</div>
<script>
function toggleOfficial() {
    document.getElementById('officialFields').style.display =
        document.getElementById('type').value === 'OFFICIAL' ? 'block' : 'none';
}
</script>
</body>
</html>
