package org.sitenv.spring.auth;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.json.JSONObject;
import org.sitenv.spring.model.DafAuthtemp;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.model.DafPatient;
import org.sitenv.spring.model.DafUserRegister;
import org.sitenv.spring.service.AuthTempService;
import org.sitenv.spring.service.ClientRegistrationService;
import org.sitenv.spring.service.PatientService;
import org.sitenv.spring.service.UserRegistrationService;
import org.sitenv.spring.util.CommonUtil;
import org.slf4j.Logger;
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
    private AuthTempService authTempService;

    @Autowired
    private ClientRegistrationService service;

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserRegistrationService userService;
    
    /** This method verifies the scopes , if scopes is valid
     * then redirect to authentication page , otherwise error 
     * message is rendered
     * 
     * @param request
     * @param response
     * @throws IOException
     */

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

        if (client_id == null || client == null) {
            response.sendRedirect(redirect_uri + "?error=invalid_client_id&error_description=Unauthorized - Invalid client_id&state=" + state);
        } else if (scope == null) {
            response.sendRedirect(redirect_uri + "?error=invalid_scope&error_description=scope is missing&state=" + state);
        } else if (state == null) {
            response.sendRedirect(redirect_uri + "?error=invalid_state&error_description=state is missing");
        } else if (redirect_uri == null) {
            response.sendError(401, "Unauthorized - redirect_uri is missing");
        } else if (response_type == null) {
            response.sendRedirect(redirect_uri + "?error=invalid_response_type&error_description=response_type is missing&state=" + state);
        } else {
            String registeredScopes = client.getScope().replace(" ", ",");

            List<String> scopes = Arrays.asList(registeredScopes.split(","));
            logger.info("Scopes: " + scope);
            //scope = scope.replace(" ", ",");
            scope = scope.replaceAll("\\s+", ",");
            List<String> reqScopes = Arrays.asList(scope.split(","));

            if (scopes.containsAll(reqScopes)) {

                try {
                    OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
                    
                  //It has 3 method :-  1.accessToken 2.refreshToken 3.authorizationCode
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

                        authTempService.saveOrUpdate(auth);

                        String baseUrl = Common.getBaseUrl(request);
                        HashMap<String, Integer> sessionObj = (HashMap<String, Integer>) session.getAttribute("user" + client.getUserId());

                        Integer currentTime = (int) (System.currentTimeMillis() / 1000L);

                        if (sessionObj == null || currentTime > sessionObj.get("expiry")) {

                            String url = baseUrl + "/login.jsp?client_id=" + client_id + "&response_type=" + response_type + "&redirect_uri=" + redirect_uri + "&scope=" + scope + "&state=" + state + "&transaction_id=" + auth.getTransaction_id().trim();
                            response.sendRedirect(url.trim());
                        } else {
                            DafUserRegister user = userService.getUserById(client.getUserId());
                            String url = baseUrl + "/authentication.jsp?client_id=" + client_id + "&response_type=" + response_type + "&redirect_uri=" + redirect_uri + "&scope=" + scope + "&state=" + state + "&transaction_id=" + auth.getTransaction_id().trim() + "&name=" + user.getUser_full_name().trim() + "&cName=" +client.getName().trim();
                            response.sendRedirect(url);
                        }
                    } else {
                        response.sendError(401, "Unauthorized - Requested Scope not authorized for Client");
                    }

                } catch (OAuthSystemException e) {
                    e.printStackTrace();
                } catch (OAuthProblemException e) {
                    e.printStackTrace();
                }

            } else {
                String url = redirect_uri + "?error=invalid_scope&error_description=Unauthorized - Requested Scope not authorized for Client&state=" + state + "&scope=" + client.getScope().trim();
                response.sendRedirect(url);
            }
        }
    }

    /** User can allow the client or deny to access their data
     * 
     * @param request
     * @param response
     * @param transactionId
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public void getAuthentication(HttpServletRequest request, HttpServletResponse response, @RequestParam("transaction_id") String transactionId) throws IOException {

        String button = request.getParameter("Allow");

        DafAuthtemp tempAuth = authTempService.getAuthenticationById(transactionId);

        if (button != null && button.equalsIgnoreCase("Allow")) {

            if (tempAuth != null) {
                List<String> scopes = Arrays.asList(tempAuth.getScope().split(","));
                if (scopes.contains("launch/patient") || scopes.contains("launch") ) {

                    String baseUrl = Common.getBaseUrl(request);
                    String url = baseUrl + "/view/patientlist.html?transaction_id=" + tempAuth.getTransaction_id().trim();
                    response.sendRedirect(url.trim());

                } else {
                    String url = tempAuth.getRedirect_uri().trim() + "?code=" + tempAuth.getAuthCode().trim() + "&state=" + tempAuth.getState().trim();
                    response.sendRedirect(url);
                }
            }
        } else {
            String url = tempAuth.getRedirect_uri().trim() + "?error=Access Denied&error_description=Access Denied&state=" + tempAuth.getState().trim();
            response.sendRedirect(url.trim());
        }
    }

    @RequestMapping(value = "/launchpatient", method = RequestMethod.GET)
    @ResponseBody
    public List<DafPatient> getAllPatientsList() {
        return patientService.getPatientsOnAuthorize();
    }

    @RequestMapping(value = "/launchpatient", method = RequestMethod.POST)
    @ResponseBody
    public void authorizeAfterLaunchPatient(HttpServletRequest request, HttpServletResponse response, @RequestParam("transaction_id") String transactionId, @RequestParam("id") String launchPatientId) throws IOException, ServletException {
        DafAuthtemp tempAuth = authTempService.getAuthenticationById(transactionId);
        tempAuth.setLaunchPatientId(launchPatientId);
        authTempService.saveOrUpdate(tempAuth);

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
    public void validateUser(HttpServletRequest request, HttpServletResponse response, @RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("transaction_id") String transactionId) throws Exception {

        DafUserRegister user = userService.getUserByDetails(userName, password, request);
        DafAuthtemp tempAuth = authTempService.getAuthenticationById(transactionId);
        String baseUrl = Common.getBaseUrl(request);
        if((user != null) && (tempAuth != null)) {
        DafClientRegister client=service.getClient(tempAuth.getClient_id());
        int clientUserId=client.getUserId();
        int userId=user.getUser_id();
        
		if (clientUserId == userId) {
			
            String url = baseUrl + "/authentication.jsp?client_id=" + tempAuth.getClient_id() + "&redirect_uri=" + tempAuth.getRedirect_uri().trim() + "&scope=" + tempAuth.getScope().trim() + "&state=" + tempAuth.getState().trim() + "&transaction_id=" + tempAuth.getTransaction_id().trim() + "&name=" + user.getUser_full_name().trim()+ "&cName=" +client.getName().trim();
            response.sendRedirect(url);
        } else {
            String url = baseUrl + "/login.jsp?error=Invalid Username or Password.&transaction_id=" + transactionId.trim();
            response.sendRedirect(url);
        } 
	}else {
            String url = baseUrl + "/login.jsp?error=Invalid Username or Password.&transaction_id=" + transactionId.trim();
            response.sendRedirect(url);
        }
    }
}
