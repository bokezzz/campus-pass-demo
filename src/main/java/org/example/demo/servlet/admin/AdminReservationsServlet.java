package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.ReservationDao;
import org.example.demo.model.AdminRole;
import org.example.demo.model.AdminUser;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 后台预约管理控制器。
 *
 * <p>同一个 Servlet 支持社会公众预约和公务预约两类管理页面。
 * type=PUBLIC 查询社会公众预约，type=OFFICIAL 查询公务预约。
 * 控制器负责权限判断、接收查询条件、调用 DAO 返回列表和统计结果。</p>
 */
@WebServlet("/admin/reservations")
public class AdminReservationsServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminUser admin = (AdminUser) request.getSession().getAttribute("admin");
        // 未传 type 时默认进入社会公众预约管理页面。
        ReservationType type = ReservationType.valueOf(request.getParameter("type") == null ? "PUBLIC" : request.getParameter("type"));
        try {
            if (type == ReservationType.PUBLIC && !(admin.getRole() == AdminRole.SCHOOL_ADMIN || admin.getRole() == AdminRole.SYSTEM_ADMIN || admin.isPublicReservationPermission())) {
                response.sendError(403, "无社会公众预约管理权限");
                return;
            }
            request.setAttribute("type", type);
            request.setAttribute("statuses", ReservationStatus.values());
            request.setAttribute("reservations", reservationDao.search(type, admin, request.getParameter("keyword"), request.getParameter("campus"), request.getParameter("status"), request.getParameter("visitDate")));
            request.setAttribute("stats", reservationDao.statistics(type, request.getParameter("dimension") == null ? "applyMonth" : request.getParameter("dimension"), admin));
            auditLogDao.log(admin.getLoginName(), "查询预约", type.getLabel(), request.getRemoteAddr());
            request.getRequestDispatcher("/WEB-INF/views/admin/reservations.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
