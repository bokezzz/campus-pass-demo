package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AdminUserDao;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.model.AdminUser;
import org.example.demo.util.SecurityUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private final AdminUserDao adminUserDao = new AdminUserDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // GET 请求只负责显示登录页面。
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        try {
            // 按登录名读取账号信息，后续再进行锁定判断和密码摘要比对。
            AdminUser user = adminUserDao.findByLoginName(loginName);
            if (user == null) {
                request.setAttribute("error", "账号或密码错误");
                auditLogDao.log(loginName, "后台登录失败", "账号不存在", request.getRemoteAddr());
                doGet(request, response);
                return;
            }
            if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
                request.setAttribute("error", "账号已锁定，请 30 分钟后再试");
                doGet(request, response);
                return;
            }
            if (!user.getPasswordHash().equals(SecurityUtil.sm3(password))) {
                adminUserDao.recordLoginFailure(user);
                request.setAttribute("error", "账号或密码错误，连续 5 次失败将锁定 30 分钟");
                auditLogDao.log(loginName, "后台登录失败", "密码错误", request.getRemoteAddr());
                doGet(request, response);
                return;
            }
            adminUserDao.clearLoginFailure(user.getId());
            request.getSession().setMaxInactiveInterval(30 * 60);
            request.getSession().setAttribute("admin", user);
            auditLogDao.log(user.getLoginName(), "后台登录", "登录成功", request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
