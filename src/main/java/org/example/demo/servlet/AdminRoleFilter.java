package org.example.demo.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demo.model.AdminUser;
import org.example.demo.model.ReservationType;
import org.example.demo.util.AdminAccessPolicy;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminRoleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (path.equals("/admin/login") || path.startsWith("/admin/assets")) {
            chain.doFilter(request, response);
            return;
        }

        AdminUser admin = (AdminUser) httpRequest.getSession().getAttribute("admin");
        if (admin == null) {
            chain.doFilter(request, response);
            return;
        }

        ReservationType reservationType = "OFFICIAL".equals(httpRequest.getParameter("type"))
                ? ReservationType.OFFICIAL : ReservationType.PUBLIC;
        if (!AdminAccessPolicy.canAccess(path, reservationType, admin.getRole(), admin.isPublicReservationPermission())) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/admin/forbidden.jsp").forward(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }
}
