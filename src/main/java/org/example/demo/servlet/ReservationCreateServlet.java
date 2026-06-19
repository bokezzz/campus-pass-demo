package org.example.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.ReservationDao;
import org.example.demo.model.Reservation;
import org.example.demo.model.ReservationType;
import org.example.demo.service.ReservationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/reservation/create")
public class ReservationCreateServlet extends HttpServlet {
    private final ReservationDao reservationDao = new ReservationDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 将用户提交的表单字段封装到预约实体中，避免后续 DAO 直接依赖 request。
        Reservation reservation = new Reservation();
        reservation.setType(ReservationType.valueOf(request.getParameter("type")));
        reservation.setCampus(request.getParameter("campus"));
        reservation.setVisitTime(LocalDateTime.parse(request.getParameter("visitTime")));
        reservation.setOrganization(request.getParameter("organization"));
        reservation.setVisitorName(request.getParameter("visitorName"));
        reservation.setIdentityNo(request.getParameter("identityNo"));
        reservation.setPhone(request.getParameter("phone"));
        reservation.setTrafficType(request.getParameter("trafficType"));
        reservation.setCarNo(request.getParameter("carNo"));
        reservation.setCompanions(request.getParameter("companions"));
        if (reservation.getType() == ReservationType.OFFICIAL) {
            reservation.setVisitDepartmentId(Long.valueOf(request.getParameter("visitDepartmentId")));
            reservation.setHostName(request.getParameter("hostName"));
            reservation.setReason(request.getParameter("reason"));
        }
        ReservationService.prepareNewReservation(reservation);
        try {
            long id = reservationDao.save(reservation);
            auditLogDao.log(reservation.getVisitorName(), "提交预约", "预约编号：" + id, request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/pass-code?id=" + id);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
