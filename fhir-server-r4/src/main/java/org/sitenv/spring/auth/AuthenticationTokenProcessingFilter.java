package org.sitenv.spring.auth;


import org.sitenv.spring.model.DafAuthtemp;
import org.sitenv.spring.service.AuthTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private AuthTempService authTempService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String authToken = httpRequest.getHeader("Authorization");
        String method = httpRequest.getMethod();


        if (authToken != null && "bearer".equalsIgnoreCase(authToken.split(" ", 2)[0].toString())) {
            authToken = authToken.substring(7);
            DafAuthtemp authentication = authTempService.validateAccessToken(authToken);

            if (authentication != null && authToken.equalsIgnoreCase(authentication.getAccess_token())) {
                try {
                    String expiryTime = authentication.getExpiry();
                    Integer currentTime = Common.convertTimestampToUnixTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())));
                    if (Common.convertTimestampToUnixTime(expiryTime) + 1800 > currentTime) {
                        List<String> scopes = Arrays.asList(authentication.getScope().split(","));
                       	if ((scopes.contains("user/*.*") || scopes.contains("user/*.read") || scopes.contains("patient/*.*") || scopes.contains("patient/*.read")) && httpRequest.getRequestURI().contains("/fhir/")){
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                       	} else if ((scopes.contains("patient/Patient.read") || scopes.contains("user/Patient.read")) && httpRequest.getRequestURI().contains("/fhir/Patient")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/AllergyIntolerance.read") || scopes.contains("user/AllergyIntolerance.read") )&& httpRequest.getRequestURI().contains("/fhir/AllergyIntolerance")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/CarePlan.read") || scopes.contains("user/CarePlan.read") )&& httpRequest.getRequestURI().contains("/fhir/CarePlan")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/CareTeam.read") || scopes.contains("user/CareTeam.read") )&& httpRequest.getRequestURI().contains("/fhir/CareTeam")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Condition.read") || scopes.contains("user/Condition.read"))&& httpRequest.getRequestURI().contains("/fhir/Condition")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Device.read") || scopes.contains("user/Device.read"))&& httpRequest.getRequestURI().contains("/fhir/Device")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/DiagnosticReport.read") || scopes.contains("user/DiagnosticReport.read"))&& httpRequest.getRequestURI().contains("/fhir/DiagnosticReport")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/DocumentReference.read") || scopes.contains("user/DocumentReference.read")) && httpRequest.getRequestURI().contains("/fhir/DocumentReference")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Encounter.read") || scopes.contains("user/Encounter.read")) && httpRequest.getRequestURI().contains("/fhir/Encounter")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Goal.read") || scopes.contains("user/Goal.read")) && httpRequest.getRequestURI().contains("/fhir/Goal")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Immunization.read") || scopes.contains("user/Immunization.read")) && httpRequest.getRequestURI().contains("/fhir/Immunization")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Location.read") || scopes.contains("user/Location.read")) && httpRequest.getRequestURI().contains("/fhir/Location")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Medication.read") || scopes.contains("user/Medication.read")) && httpRequest.getRequestURI().contains("/fhir/Medication")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/MedicationRequest.read") || scopes.contains("user/MedicationRequest.read")) && httpRequest.getRequestURI().contains("/fhir/MedicationRequest")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Observation.read") || scopes.contains("user/Observation.read")) && httpRequest.getRequestURI().contains("/fhir/Observation")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Organization.read") || scopes.contains("user/Organization.read")) && httpRequest.getRequestURI().contains("/fhir/Organization")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Practitioner.read") || scopes.contains("user/Practitioner.read")) && httpRequest.getRequestURI().contains("/fhir/Practitioner")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/PractitionerRole.read") || scopes.contains("user/PractitionerRole.read")) && httpRequest.getRequestURI().contains("/fhir/PractitionerRole")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Procedure.read") || scopes.contains("user/Procedure.read")) && httpRequest.getRequestURI().contains("/fhir/Procedure")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/Provenance.read") || scopes.contains("user/Provenance.read")) && httpRequest.getRequestURI().contains("/fhir/Provenance")) {
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
                            chain.doFilter(request, response);
                        } else if ((scopes.contains("patient/RelatedPerson.read") || scopes.contains("user/RelatedPerson.read")) && httpRequest.getRequestURI().contains("/fhir/Provenance")) {
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
        }else if (httpRequest.getRequestURI().contains("/fhir/.well-known")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            getServletContext().getRequestDispatcher("/.well-known/smart-configuration").forward(request, response);
        }else if (httpRequest.getRequestURI().contains("/jwk")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        }
        /*else if (httpRequest.getRequestURI().contains("/r4")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        }*/
        else if (httpRequest.getRequestURI().contains("/open")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        }else if (httpRequest.getRequestURI().contains("/introspect")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains("/authorize")) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password", authorities));
            chain.doFilter(request, response);
        } else if (httpRequest.getServletPath().contains("/token")) {
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
        } else if (httpRequest.getServletPath().contains("/fonts")) {
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
