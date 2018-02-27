package org.sitenv.spring.auth;

import ch.qos.logback.classic.Logger;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.json.JSONObject;
import org.sitenv.spring.dao.AuthTempDao;
import org.sitenv.spring.dao.PatientDao;
import org.sitenv.spring.model.DafAuthtemp;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.model.DafUserRegister;
import org.sitenv.spring.model.PatientList;
import org.sitenv.spring.service.ClientRegistrationService;
import org.sitenv.spring.service.UserRegistrationService;
import org.sitenv.spring.util.CommonUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/authorize")
public class AuthorizeEndPoint extends HttpServlet {

    private static final long serialVersionUID = 1L;

    Logger logger = (Logger) LoggerFactory.getLogger(AuthorizeEndPoint.class);

    @Autowired
    private AuthTempDao dao;

    @Autowired
    private ClientRegistrationService service;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private UserRegistrationService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    @SuppressWarnings("unchecked")
    public void getAuthorization(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.info("Authorize url: " + request.getRequestURL().append('?').append(request.getQueryString()));
        HttpSession session = request.getSession();

        String response_type = request.getParameter("response_type");
        String client_id = request.getParameter("client_id");   //the id of the client
        String client_secret = request.getParameter("client_secret");
        String redirect_uri = request.getParameter("redirect_uri");
        String scope = request.getParameter("scope");
        String state = request.getParameter("state");
        
        DafClientRegister client = service.getClient(client_id);
        //Newly added code to resolve scopes issue
        if(client != null){
            
            String registeredScopes = client.getScope().replace(" ", ",");
        
        List<String> scopes = Arrays.asList(registeredScopes.split(","));
        logger.info("Scopes: "+scope);
        scope = scope.replace(" ", ",");
        List<String> reqScopes = Arrays.asList(scope.split(","));
        if (reqScopes == null) {
            response.sendRedirect(redirect_uri + "?error=invalid_scope&error_description=Scope is missing&state=" + state);
        }

        if (scopes.containsAll(reqScopes)) {

            try {
                OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
			 

                if (responseType.equals(ResponseType.CODE.toString())) {

                    String authorizationCode = oauthIssuerImpl.authorizationCode();
                    logger.info("Authorization Endpoint -> code :" + authorizationCode);

                    DafAuthtemp auth = new DafAuthtemp();
                    auth.setClient_id(client_id);
                    auth.setClient_secret(client_secret);
                    auth.setRedirect_uri(redirect_uri);
                    auth.setScope(scope);
                    auth.setAuthCode(authorizationCode);
                    auth.setState(state);
                    auth.setTransaction_id(CommonUtil.generateRandomString(5));

                    dao.saveOrUpdate(auth);

                    String uri = request.getScheme() + "://" +
	                        request.getServerName() + 
	                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
	                        +request.getContextPath();
                    HashMap<String, Integer> sessionObj = (HashMap<String, Integer>) session.getAttribute("user" + client.getUserId());

                    Integer currentTime = (int) (System.currentTimeMillis() / 1000L);
                    
                    if (sessionObj == null || currentTime > sessionObj.get("expiry")) {
                        
                        String url = uri + "/login.jsp?client_id=" + client_id + "&response_type=" + response_type + "&redirect_uri=" + redirect_uri + "&scope=" + scope + "&state=" + state + "&transaction_id=" + auth.getTransaction_id().trim();
                        response.sendRedirect(url.trim());
                    } else {
                    	System.out.println("in auth.jsp");
                    	DafUserRegister user = userService.getUserById(client.getUserId());
                        String url = uri + "/authentication.jsp?client_id=" + client_id + "&response_type=" + response_type + "&redirect_uri=" + redirect_uri + "&scope=" + scope + "&state=" + state + "&transaction_id=" + auth.getTransaction_id().trim()+"&name="+user.getUser_full_name().trim();
                        response.sendRedirect(url);
                    }
                } else {
                    response.sendError(401, "Unauthorized - Requested Scope not authorized for Client");
                }
	            
	            /*if (responseType.equals(ResponseType.TOKEN.toString())) {
	                final String accessToken = oauthIssuerImpl.accessToken();
	                database.addToken(accessToken);

	                builder.setAccessToken(accessToken);
	                builder.setExpiresIn(3600l);
	            }*/

            } catch (OAuthSystemException e) {
                e.printStackTrace();
            } catch (OAuthProblemException e) {
                e.printStackTrace();
            }

        } else {
            String url = redirect_uri + "?error=invalid_scope&error_description=Unauthorized - Requested Scope not authorized for Client&state=" + state + "&scope=" + client.getScope().trim();
            response.sendRedirect(url);
        }
        //Newly added code to resolve scopes issue
        }else{
        	response.sendRedirect(redirect_uri+"?error=invalid_client_id&error_description=Unauthorized - Invalid client_id&state="+state);
        }
    }
	

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public void getAuthentication(HttpServletRequest request, HttpServletResponse response, @RequestParam("transaction_id") String transactionId) throws IOException {

    	String button = request.getParameter("Allow");
       
        DafAuthtemp tempAuth = dao.getAuthenticationById(transactionId);
       
        if (button!= null && button.equalsIgnoreCase("Allow")) {
            
            if (tempAuth != null) {
                List<String> scopes = Arrays.asList(tempAuth.getScope().split(","));
                if (scopes.contains("launch/patient")) {

                	String uri = request.getScheme() + "://" +
	                        request.getServerName() + 
	                        ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
	                        +request.getContextPath();
                    String url = uri + "/view/patientlist.html?transaction_id=" + tempAuth.getTransaction_id().trim();
                    response.sendRedirect(url.trim());

                } else {
                    String url = tempAuth.getRedirect_uri().trim() + "?code=" + tempAuth.getAuthCode().trim() + "&state=" + tempAuth.getState().trim();
                    response.sendRedirect(url);
                }
            }
        } else {
            String url = tempAuth.getRedirect_uri().trim() + "?error=Access Denied&error_description=Access Denied&state="+tempAuth.getState().trim();
            response.sendRedirect(url.trim());
        }
    }

    @RequestMapping(value = "/launchpatient", method = RequestMethod.GET)
    @ResponseBody
    public List<PatientList> getAllPatientsList() {
        return patientDao.getPatientsOnAuthorize();
    }

    @RequestMapping(value = "/launchpatient", method = RequestMethod.POST)
    @ResponseBody
    public void authorizeAfterLaunchPatient(HttpServletRequest request, HttpServletResponse response, @RequestParam("transaction_id") String transactionId, @RequestParam("id") Integer launchPatientId) throws IOException, ServletException {
        DafAuthtemp tempAuth = dao.getAuthenticationById(transactionId);
        tempAuth.setLaunchPatientId(launchPatientId);
        dao.saveOrUpdate(tempAuth);

        String url = tempAuth.getRedirect_uri().trim() + "?code=" + tempAuth.getAuthCode().trim() + "&state=" + tempAuth.getState().trim();
        logger.info("Authorize Endpoint -> formed URL: " + url);
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();
        json.put("redirect_url", url);
        response.addHeader("Content-Type", "application/json");
        out.println(json.toString());
    }

    @RequestMapping(value = "/userValidate", method = RequestMethod.POST)
    @ResponseBody
    public void validateUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("transaction_id") String transactionId) throws IOException {
        
        DafUserRegister user = userService.getUserByDetails(userName, password, request);
        DafAuthtemp tempAuth = dao.getAuthenticationById(transactionId);
        String uri = request.getScheme() + "://" +
                request.getServerName() + 
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() )
                +request.getContextPath();
        if (user != null) {
            
            String url = uri + "/authentication.jsp?client_id=" + tempAuth.getClient_id() + "&redirect_uri=" + tempAuth.getRedirect_uri().trim() + "&scope=" + tempAuth.getScope().trim() + "&state=" + tempAuth.getState().trim() + "&transaction_id=" + tempAuth.getTransaction_id().trim()+"&name="+user.getUser_full_name().trim();
            response.sendRedirect(url);
        } else {
        	String url = uri+"/login.jsp?error=Invalid Username or Password.&transaction_id="+transactionId.trim();
			response.sendRedirect(url);
        }
    }
}
