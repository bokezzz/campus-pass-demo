package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.ReservationDao;
import org.example.demo.model.AdminUser;
import org.example.demo.model.ReservationType;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminUser admin = (AdminUser) request.getSession().getAttribute("admin");
        try {
            // 首页只展示两组摘要统计，详细查询在预约管理页面完成。
            request.setAttribute("publicStats", reservationDao.statistics(ReservationType.PUBLIC, "campus", admin));
            request.setAttribute("officialStats", reservationDao.statistics(ReservationType.OFFICIAL, "department", admin));
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
