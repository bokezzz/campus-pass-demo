package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.ReservationDao;
import org.example.demo.model.AdminUser;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;
import org.example.demo.util.AdminAccessPolicy;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/reservations")
public class AdminReservationsServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminUser admin = (AdminUser) request.getSession().getAttribute("admin");
        ReservationType type = ReservationType.valueOf(request.getParameter("type") == null ? "PUBLIC" : request.getParameter("type"));
        try {
            if (!AdminAccessPolicy.canAccess("/admin/reservations", type, admin.getRole(), admin.isPublicReservationPermission())) {
                request.getRequestDispatcher("/WEB-INF/views/admin/forbidden.jsp").forward(request, response);
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
