package org.example.demo.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.dao.AuditLogDao;
import org.example.demo.dao.DepartmentDao;
import org.example.demo.model.Department;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 后台部门管理控制器。
 *
 * <p>用于维护学校部门、学院等信息。公务预约需要选择访问部门，
 * 公务预约统计也可以按部门分组，因此部门管理是公务预约模块的基础数据。</p>
 */
@WebServlet("/admin/departments")
public class DepartmentManageServlet extends HttpServlet {
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String editId = request.getParameter("edit");
            if (editId != null) {
                // edit 参数存在时表示进入编辑模式，需要把原部门信息回显到表单。
                request.setAttribute("editDepartment", departmentDao.findById(Long.parseLong(editId)));
            }
            request.setAttribute("departments", departmentDao.findAll());
            request.getRequestDispatcher("/WEB-INF/views/admin/departments.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("delete".equals(request.getParameter("action"))) {
                departmentDao.delete(Long.parseLong(request.getParameter("id")));
                auditLogDao.log(actor(request), "删除部门", "部门ID：" + request.getParameter("id"), request.getRemoteAddr());
            } else {
                Department department = new Department();
                if (request.getParameter("id") != null && !request.getParameter("id").isBlank()) {
                    department.setId(Long.parseLong(request.getParameter("id")));
                }
                department.setCode(request.getParameter("code"));
                department.setType(request.getParameter("type"));
                department.setName(request.getParameter("name"));
                departmentDao.save(department);
                auditLogDao.log(actor(request), "保存部门", department.getName(), request.getRemoteAddr());
            }
            response.sendRedirect(request.getContextPath() + "/admin/departments");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private String actor(HttpServletRequest request) {
        return ((org.example.demo.model.AdminUser) request.getSession().getAttribute("admin")).getLoginName();
    }
}
