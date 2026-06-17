package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 审计日志页面控制器。
 *
 * <p>审计管理员或系统管理员可以查看系统关键操作记录，
 * 包括登录、查看通行码、预约查询、审核等。</p>
 */
@WebServlet("/admin/logs")
public class AdminLogsServlet extends HttpServlet {
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 查询最近的审计日志并交给 JSP 渲染。
            request.setAttribute("logs", auditLogDao.findAll());
            request.getRequestDispatcher("/WEB-INF/views/admin/logs.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
