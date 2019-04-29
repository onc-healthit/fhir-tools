package org.sitenv.spring.auth;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.minidev.json.JSONObject;

@Controller
@RequestMapping("jwk")
public class JWKSetPublishingEndpoint extends HttpServlet {

	@Autowired
	private JwtGenerator jwtGenerator;
	private static final long serialVersionUID = 1L;
	public static final String URL = "jwk";

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, List<JSONObject>> getJwk() {
		Map<String, List<JSONObject>> keys = jwtGenerator.getAllPublicKeys();
		return keys;
	}
}
