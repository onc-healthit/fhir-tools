package org.sitenv.spring.auth;

import org.sitenv.spring.model.DafAuthtemp;
import org.sitenv.spring.service.AuthTempService;
import org.sitenv.spring.service.ClientRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
/*
 * This class provides the basic information about 
 * such as expire time, issued time , active ,
 * token type etc.
 *
 */

@Controller
@RequestMapping("/introspect")
public class IntrospectionEndpoint extends HttpServlet {

	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 4919755524639593435L;

	@Autowired
	private ClientRegistrationService clientRegistrationService;

	@Autowired
	private AuthTempService authTempService;

	Logger logger = (Logger) LoggerFactory.getLogger(IntrospectionEndpoint.class);
	
	/** Provides description about access token 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> introspection(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.info("Introspect url: " + request.getRequestURL().append('?').append(request.getQueryString()));
		Map<String, Object> m = new HashMap<String, Object>();
		String accessToken = request.getParameter("token");

		if (accessToken == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND , "access token is missing");
		}

		DafAuthtemp authData = authTempService.validateAccessToken(accessToken);
		if (authData == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND , "access token is invalid");
		}

		if (authData != null) {
			try {
				String expiryTime = authData.getExpiry();
				Integer currentTime = Common.convertTimestampToUnixTime(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())));
				if (Common.convertTimestampToUnixTime(expiryTime) + 3600 > currentTime) {
					m.put("exp", Common.convertTimestampToUnixTime(expiryTime) + 3600);
					m.put("sub", clientRegistrationService.getClient(authData.getClient_id()).getContact_name());
					m.put("grant_type", "authorization_code");
					m.put("active", "true" );
					m.put("token_type", "Bearer" );
					m.put("client_id", authData.getClient_id());
					m.put("iat", Common.convertTimestampToUnixTime(expiryTime));
					response.addHeader("Content-Type", "application/json");
					response.addHeader("Cache-Control", "no-store");
					response.addHeader("Pragma", "no-cache");
					
				} else {
					m.put("active ", "false");
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return m;
	}
}
