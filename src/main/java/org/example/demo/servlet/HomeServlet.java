package org.example.demo.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.util.DbUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 系统首页控制器。
 *
 * <p>访问应用根路径时先初始化数据库，再转发到手机端首页。
 * 这样首次运行项目时可以自动创建表和演示账号。</p>
 */
@WebServlet("")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 首次访问系统时确保数据库表和演示数据已经准备好。
            DbUtil.initialize();
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
