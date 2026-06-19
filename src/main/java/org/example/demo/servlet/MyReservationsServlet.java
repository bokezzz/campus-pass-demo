package org.example.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.ReservationDao;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/my-reservations")
public class MyReservationsServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 首次进入页面时只展示查询表单，不执行数据库查询。
        request.getRequestDispatcher("/WEB-INF/views/my-reservations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 查询结果放入 request，JSP 使用 reservations 属性渲染表格。
            request.setAttribute("reservations", reservationDao.findByVisitor(
                    request.getParameter("visitorName"),
                    request.getParameter("identityNo"),
                    request.getParameter("phone")));
            auditLogDao.log(request.getParameter("visitorName"), "查询本人预约", "手机端查询历史预约", request.getRemoteAddr());
            request.getRequestDispatcher("/WEB-INF/views/my-reservations.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
