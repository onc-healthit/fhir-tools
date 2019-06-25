package org.sitenv.spring.auth;

import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import java.util.List;
import java.util.Map;


/*
 * Provides list of public key(s) for JSON web token
 * which could be used to verify JWT id token 
 * 
 */
@Controller
@RequestMapping("jwk")
public class JWKSetPublishingEndpoint extends HttpServlet {

	@Autowired
	private JwtGenerator jwtGenerator;
	private static final long serialVersionUID = 1L;
	public static final String URL = "jwk";
	
	/** Provides JSON web key 
	 * 
	 * @return
	 */

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, List<JSONObject>> getJwk() {
		Map<String, List<JSONObject>> keys = jwtGenerator.getAllPublicKeys();
		return keys;
	}
}
