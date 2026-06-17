<%@ page contentType="text/html;charset=UTF-8" import="java.util.*,org.example.demo.model.*,org.example.demo.util.SecurityUtil" %>
<%
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
    List<StatisticRow> stats = (List<StatisticRow>) request.getAttribute("stats");
    ReservationType type = (ReservationType) request.getAttribute("type");
    ReservationStatus[] statuses = (ReservationStatus[]) request.getAttribute("statuses");
%>
<!doctype html>
<html>
<head><title>预约管理</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css"></head>
<body>
<jsp:include page="nav.jsp"/>
<div class="shell">
    <div class="panel">
        <h1><%=type.getLabel()%></h1>
        <form method="get">
            <input type="hidden" name="type" value="<%=type.name()%>">
            <div class="grid">
                <div><label>关键词</label><input name="keyword" placeholder="姓名/身份证/单位/接待人"></div>
                <div><label>预约校区</label><select name="campus"><option value="">全部</option><option>朝晖校区</option><option>屏峰校区</option><option>莫干山校区</option></select></div>
                <div><label>审核状态</label><select name="status"><option value="">全部</option><% for (ReservationStatus s : statuses) { %><option value="<%=s.name()%>"><%=s.getLabel()%></option><% } %></select></div>
                <div><label>进校日期</label><input type="date" name="visitDate"></div>
                <div><label>统计维度</label><select name="dimension"><option value="applyMonth">申请月度</option><option value="visitMonth">预约月度</option><option value="campus">预约校区</option><option value="department">访问部门</option></select></div>
            </div>
            <p><button type="submit">查询/统计</button></p>
        </form>
    </div>
    <div class="panel">
        <h2>统计结果</h2>
        <table><tr><th>维度</th><th>预约次数</th><th>预约人次</th></tr>
            <% for (StatisticRow row : stats) { %><tr><td><%=row.getLabel()%></td><td><%=row.getReservations()%></td><td><%=row.getPeople()%></td></tr><% } %>
        </table>
    </div>
    <div class="panel">
        <h2>预约列表</h2>
        <table>
            <tr><th>申请时间</th><th>校区/进校时间</th><th>单位</th><th>姓名</th><th>身份证</th><th>公务信息</th><th>状态</th><th>操作</th></tr>
            <% for (Reservation r : reservations) { %>
            <tr>
                <td><%=r.getApplyTime()%></td>
                <td><%=r.getCampus()%><br><%=r.getVisitTime()%></td>
                <td><%=r.getOrganization()%></td>
                <td><%=SecurityUtil.maskName(r.getVisitorName())%></td>
                <td><%=SecurityUtil.maskIdentityNo(r.getIdentityNo())%></td>
                <td><%=r.getVisitDepartmentName() == null ? "" : r.getVisitDepartmentName()%><br><%=r.getHostName() == null ? "" : r.getHostName()%><br><%=r.getReason() == null ? "" : r.getReason()%></td>
                <td><span class="badge"><%=r.getStatus().getLabel()%></span></td>
                <td>
                    <a href="${pageContext.request.contextPath}/pass-code?id=<%=r.getId()%>">通行码</a>
                    <% if (type == ReservationType.OFFICIAL && r.getStatus() == ReservationStatus.PENDING) { %>
                    <form method="post" action="${pageContext.request.contextPath}/admin/review">
                        <input type="hidden" name="id" value="<%=r.getId()%>">
                        <select name="status"><option value="APPROVED">通过</option><option value="REJECTED">驳回</option></select>
                        <input name="comment" placeholder="审核意见">
                        <button type="submit">审核</button>
                    </form>
                    <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>
</div>
</body>
</html>
