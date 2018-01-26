package org.sitenv.spring.util;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CorsFilters implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest request = (HttpServletRequest) req;
    	HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Prefer, Authorization");
        response.setHeader("access-control-expose-headers", "Content-Location,Link,X-Progress,Prefer,Origin, X-Requested-With, Content-Type, Accept,Access-Control-Max-Age,Access-Control-Allow-Methods,Access-Control-Allow-Origin");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }   
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }
}