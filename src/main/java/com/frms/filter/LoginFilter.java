package com.frms.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String url = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/"));
        //System.out.println(url);
        Object obj = session.getAttribute("user");
        if(url.equals("/toLogin") || url.equals("/login")|| url.equals("/toRegister") || url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            filterChain.doFilter(servletRequest, servletResponse);
        }else{
            if(obj==null) {
                response.sendRedirect("/frms/toLogin");
            }else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
