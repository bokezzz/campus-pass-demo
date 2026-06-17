package org.example.demo.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 后台登录拦截过滤器。
 *
 * <p>所有 /admin/* 请求都会先经过这里。除了登录页本身，
 * 其他后台页面必须先登录并在 Session 中保存 admin 对象，否则重定向到登录页。</p>
 */
@WebFilter("/admin/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // 登录入口不需要拦截，否则用户永远无法进入登录页。
        if (path.equals("/admin/login") || path.startsWith("/admin/assets")) {
            chain.doFilter(request, response);
            return;
        }

        // Session 中没有 admin 说明尚未登录或会话已超时。
        if (httpRequest.getSession().getAttribute("admin") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login");
            return;
        }
        chain.doFilter(request, response);
    }
}
