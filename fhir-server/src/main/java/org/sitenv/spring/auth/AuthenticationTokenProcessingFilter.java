package org.sitenv.spring.auth;


import org.sitenv.spring.dao.AuthTempDao;
import org.sitenv.spring.model.DafAuthtemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    @Autowired
    private AuthTempDao dao;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpServletRequest httpRequest = (HttpServletRequest) request;


        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
        String authToken = httpRequest.getHeader("Authorization");
        String method = httpRequest.getMethod();


        if (authToken != null && "bearer".equalsIgnoreCase(authToken.split(" ", 2)[0].toString())) {
            authToken = authToken.substring(7);
            DafAuthtemp authentication = dao.validateAccessToken(authToken);

            if (authentication != null && authToken.equalsIgnoreCase(authentication.getAccess_token())) {
                try {
                    String expiryTime = authentication.getExpiry();
                    Integer currentTime = Common.convertTimestampToUnixTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())));
                    if (Common.convertTimestampToUnixTime(expiryTime) + 3600 > currentTime) {
                        List<String> scopes = Arrays.asList(authentication.getScope().split(","));
                        List<String> patientScopes = Arrays.asList("launch/patient,patient/Patient.read,user/Patient.read".split(","));
                        List<String> userScopes = Arrays.asList("user/*.read,user/*.*,patient/*.read,fhir_complete".split(","));

                        if (CollectionUtils.containsAny(scopes, userScopes)) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if (CollectionUtils.containsAny(scopes, patientScopes) && httpRequest.getRequestURI().contains("/fhir/Patient")) {
                            if (scopes.contains("launch/patient") && !httpRequest.getPathInfo().split("/", 3)[2].equals(String.valueOf(authentication.getLaunchPatientId()))) {
                                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access is denied for this Patient");
                            }
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if (scopes.contains("patient/DocumentReference.read") && httpRequest.getRequestURI().contains("/fhir/DocumentReference")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if (scopes.contains("patient/MedicationOrder.read") && httpRequest.getRequestURI().contains("/fhir/MedicationOrder")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if (scopes.contains("patient/MedicationStatement.read") && httpRequest.getRequestURI().contains("/fhir/MedicationStatement")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else {
                            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: scope is not valid to access the resource.");
                        }
                    } else {

                        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication token is expired.");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {

                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication token was invalid.");
            }
        }else if (httpRequest.getRequestURI().contains("/fhirserver")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getRequestURI().contains("/open")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains("/authorize")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().equals("/token")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getRequestURI().contains("/metadata")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().equals("/login")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().equals("/launch")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains("/user")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains("/client")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains(".jsp")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains(".html")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains(".js")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains(".css")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains(".png")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains("woff2") || httpRequest.getServletPath().contains("woff") || httpRequest.getServletPath().contains("ttf")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (method.equalsIgnoreCase("OPTIONS")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Authentication token not found.");
        }
    }

}