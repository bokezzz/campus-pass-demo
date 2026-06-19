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

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/review")
public class AdminReviewServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminUser admin = (AdminUser) request.getSession().getAttribute("admin");
        try {
            // 审核结果和审核意见来自后台页面表单。
            reservationDao.review(Long.parseLong(request.getParameter("id")), ReservationStatus.valueOf(request.getParameter("status")), request.getParameter("comment"));
            auditLogDao.log(admin.getLoginName(), "审核公务预约", "预约ID：" + request.getParameter("id") + "，结果：" + request.getParameter("status"), request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/reservations?type=OFFICIAL");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
