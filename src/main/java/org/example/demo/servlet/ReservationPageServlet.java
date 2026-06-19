package org.example.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.DepartmentDao;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/reserve")
public class ReservationPageServlet extends HttpServlet {
    private final DepartmentDao departmentDao = new DepartmentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("departments", departmentDao.findAll());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/reserve.jsp").forward(request, response);
    }
}
