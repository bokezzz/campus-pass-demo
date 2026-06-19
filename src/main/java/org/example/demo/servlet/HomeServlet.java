package org.example.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.util.DbUtil;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            DbUtil.initialize();
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
