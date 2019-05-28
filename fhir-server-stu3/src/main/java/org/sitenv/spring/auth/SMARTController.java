package org.sitenv.spring.auth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.inject.internal.util.Lists;

@Controller
@RequestMapping("/.well-known/*")
public class SMARTController extends HttpServlet {

	private static final long serialVersionUID = 936659617930557226L;

	@RequestMapping(value = "smart-configuration", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getWellKnowllSMATConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> wellKnownConfig= new HashMap<String, Object>();

        String uri = request.getScheme() + "://" +
                request.getServerName() +
                ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                request.getContextPath();

        wellKnownConfig.put("authorization_endpoint", uri + "/authorize");
        wellKnownConfig.put("token_endpoint", uri + "/token");


        String[] stringArrayMethods = { "client_secret_basic","client_secret_post" };
        String[] stringArrayScopes = { "openid", "profile","fhirUser","launch","launch/patient","launch/encounter","patient/*.*","user/*.*","offline_access"};
        String[] stringArrayResponce = {"code"};
        String[] stringArrayCapabilities = {"launch-ehr",
                "launch-standalone",
                "client-public",
                "client-confidential-symmetric",
                "context-passthrough-banner",
                "context-passthrough-style",
                "context-ehr-patient",
                "context-ehr-encounter",
                "context-standalone-patient",
                "context-standalone-encounter",
                "permission-offline",
                "permission-patient",
                "permission-user"};

        wellKnownConfig.put("token_endpoint_auth_methods_supported", stringArrayMethods);
        wellKnownConfig.put("scopes_supported", stringArrayScopes);
        wellKnownConfig.put("response_types_supported", stringArrayResponce);

        wellKnownConfig.put("capabilities", stringArrayCapabilities);

        return wellKnownConfig;

    }
    
    @RequestMapping(value = "openid-configuration", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAuthorization(HttpServletRequest request, HttpServletResponse response){
	
		String baseUrl = request.getScheme() + "://" + request.getServerName()
		+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
		|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
				: ":" + request.getServerPort())
		+ request.getContextPath();
		
		if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl.concat("/");
		}

		Map<String, Object> m = new HashMap<>();
		m.put("issuer", baseUrl);
		m.put("authorization_endpoint", baseUrl + "authorize");
		m.put("token_endpoint", baseUrl + "token/");
	    //m.put("userinfo_endpoint", baseUrl + UserInfoEndpoint.URL);
		m.put("jwks_uri", baseUrl+ "jwk" );
		m.put("response_types_supported", Lists.newArrayList("code")); 
		m.put("subject_types_supported", Lists.newArrayList("public"));
		m.put("claims_supported", Lists.newArrayList(
				"sub",
				"name",
				"profile",
				"email"
				));
		
		m.put("claims_parameter_supported", false);
		m.put("request_parameter_supported", false);
		m.put("request_uri_parameter_supported", true);
		m.put("require_request_uri_registration", false);
		return m;
	}
}
