package org.example.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.ReservationDao;
import org.example.demo.model.Reservation;
import org.example.demo.service.ReservationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/pass-code")
public class PassCodeServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // URL 示例：/pass-code?id=4。id 对应 reservations 表主键。
            Reservation reservation = reservationDao.findById(Long.parseLong(request.getParameter("id")));
            request.setAttribute("reservation", reservation);
            // valid 是页面显示“有效/无效”的核心标志。
            request.setAttribute("valid", ReservationService.isPassCodeValid(reservation, LocalDateTime.now()));
            if (reservation != null) {
                auditLogDao.log(reservation.getVisitorName(), "查看通行码", "预约编号：" + reservation.getId(), request.getRemoteAddr());
            }
            request.getRequestDispatcher("/WEB-INF/views/pass-code.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
