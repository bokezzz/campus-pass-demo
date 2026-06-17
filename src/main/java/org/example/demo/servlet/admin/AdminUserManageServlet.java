package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AdminUserDao;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.DepartmentDao;
import org.example.demo.model.AdminRole;
import org.example.demo.model.AdminUser;
import org.example.demo.util.SecurityUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 后台管理员管理控制器。
 *
 * <p>负责管理员账号的新增、修改、删除，以及角色、部门、授权信息的维护。
 * 密码复杂度在控制器中校验，密码 SM3 摘要在 DAO 中保存。</p>
 */
@WebServlet("/admin/users")
public class AdminUserManageServlet extends HttpServlet {
    private final AdminUserDao adminUserDao = new AdminUserDao();
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String editId = request.getParameter("edit");
            if (editId != null) {
                // edit 参数存在时表示编辑已有管理员，需要查询原数据回显。
                request.setAttribute("editUser", adminUserDao.findById(Long.parseLong(editId)));
            }
            request.setAttribute("users", adminUserDao.findAll());
            request.setAttribute("departments", departmentDao.findAll());
            request.setAttribute("roles", AdminRole.values());
            request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("delete".equals(request.getParameter("action"))) {
                adminUserDao.delete(Long.parseLong(request.getParameter("id")));
                auditLogDao.log(actor(request), "删除管理员", "管理员ID：" + request.getParameter("id"), request.getRemoteAddr());
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
            String password = request.getParameter("password");
            if ((request.getParameter("id") == null || request.getParameter("id").isBlank() || !password.isBlank()) && !SecurityUtil.isStrongPassword(password)) {
                request.setAttribute("error", "密码至少 8 位，且包含大小写字母、数字和特殊字符");
                doGet(request, response);
                return;
            }
            AdminUser user = new AdminUser();
            if (request.getParameter("id") != null && !request.getParameter("id").isBlank()) {
                user.setId(Long.parseLong(request.getParameter("id")));
            }
            user.setName(request.getParameter("name"));
            user.setLoginName(request.getParameter("loginName"));
            user.setRole(AdminRole.valueOf(request.getParameter("role")));
            String departmentId = request.getParameter("departmentId");
            user.setDepartmentId(departmentId == null || departmentId.isBlank() ? null : Long.parseLong(departmentId));
            user.setPhone(request.getParameter("phone"));
            user.setPublicReservationPermission("on".equals(request.getParameter("publicReservationPermission")));
            adminUserDao.save(user, password);
            auditLogDao.log(actor(request), "保存管理员", user.getLoginName(), request.getRemoteAddr());
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String actor(HttpServletRequest request) {
        return ((AdminUser) request.getSession().getAttribute("admin")).getLoginName();
    }
}
