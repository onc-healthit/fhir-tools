package org.sitenv.spring.auth;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.io.IOUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONObject;
import org.sitenv.spring.dao.AuthTempDao;
import org.sitenv.spring.jwtservice.JWKSCache;
import org.sitenv.spring.jwtservice.JWTService;
import org.sitenv.spring.jwtservice.JWTStoredKeyService;
import org.sitenv.spring.model.DafAuthtemp;
import org.sitenv.spring.model.DafClientRegister;
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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/token")
public class TokenEndPoint extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger log = (Logger) LoggerFactory.getLogger(TokenEndPoint.class);

	@Autowired
	private ClientRegistrationService service;
	
	@Autowired
	private Common common;

	@Autowired
	private AuthTempDao dao;

	@Autowired
	private JWKSCache validationServices;

	@RequestMapping(value = "" , method = RequestMethod.POST)
	@ResponseBody
	public void getAuthorization(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        if(map.get("content-type")!= null && map.get("accept")!= null) {
        if(map.get("content-type").equals("application/x-www-form-urlencoded") && map.get("accept").equals("application/json")) {
 
		PrintWriter out = response.getWriter();

		String client_secret = null;
		String token = null;
		String grant_type = null;
		String scope = null;
		String client_assertion_type = null;
		String client_assertion = null;
		if (request.getParameter("client_assertion_type") != null) {
			grant_type = request.getParameter("grant_type");
			scope = request.getParameter("scope");
			client_assertion = request.getParameter("client_assertion");
			client_assertion_type = request.getParameter("client_assertion_type");

		} else {
			String paramReaderString= IOUtils.toString(request.getReader());
			try {
				String   sb = java.net.URLDecoder.decode(paramReaderString, StandardCharsets.UTF_8.name());
	
			if (!sb.toString().isEmpty() && sb.toString() != null) {

				log.info("String Buffer " + sb);
				if (sb.toString().contains("client_assertion")) {

					log.info(" Found Client assertion ");
					String[] params = sb.toString().split("&");

					for (String p : params) {
						String[] pair = p.split("=");
						if ("client_assertion".equalsIgnoreCase(pair[0])) {
							client_assertion = pair[1];
						}

						if ("grant_type".equalsIgnoreCase(pair[0])) {
							grant_type = pair[1];
						}

						if ("client_assertion_type".equalsIgnoreCase(pair[0])  && (pair.length> 1)) {
							client_assertion_type = pair[1];
						}

						if ("scope".equalsIgnoreCase(pair[0]) && (pair.length> 1)) {
							scope = pair[1];
						}

					}

				}
				}		
			} catch (UnsupportedEncodingException e) {
				log.warn("UnsupportedEncodingException occured ", e);
			}	
		}		

			if (grant_type == null  || !(grant_type.equals(GrantType.CLIENT_CREDENTIALS.toString()))) {
				response.sendError(400, "grant_type is not present");
			//} else if (grant_type.equals(GrantType.CLIENT_CREDENTIALS.toString())) {
			} else	if (client_assertion == null) {
					response.sendError(400, "client_assertion is not present");
				} else if (client_assertion_type == null
						|| !(client_assertion_type.equals("urn:ietf:params:oauth:client-assertion-type:jwt-bearer"))) {
					
					response.sendError(400, "client_assertion_type is either not present or may not be valid");
					
				} 
				
				else if (scope == null ) {
					response.sendError(400, "scope is null ");
				}else {
				

				JWT jsonWebToken = JWTParser.parse(client_assertion);
				if (common.isValid(jsonWebToken)) {
					Algorithm tokenAlg = jsonWebToken.getHeader().getAlgorithm();// check later
					SignedJWT signedIdToken = (SignedJWT) jsonWebToken;
					// validate our ID Token over a number of tests
					JWTClaimsSet idClaims = jsonWebToken.getJWTClaimsSet();
					String clientIdFromJWTToken = idClaims.getSubject();
					DafClientRegister regClient = service.getClient(clientIdFromJWTToken);
					String scopes = regClient.getScope().replaceAll("\\s+", ",");
					List<String> registeredScopes = Arrays.asList(scopes.split(","));
					if(!common.isValidScopes(scope,registeredScopes, clientIdFromJWTToken)) {
						response.sendError(400, "scope is either not present or may not be valid. ");
					}
					
					
					List<String> audFromJWTToken = idClaims.getAudience();
					Header jwtHeader = jsonWebToken.getHeader();
					Object jku = jwtHeader.toJSONObject().get("jku");
					if (jku == null) {	
						JWTStoredKeyService jWTStoredKeyService = null;
						jWTStoredKeyService = validationServices.loadStoredPublicKey(clientIdFromJWTToken);

						if (jWTStoredKeyService != null) {
							if (!jWTStoredKeyService.validateSignature(signedIdToken)) {
								response.sendError(401, "public key could not validate the JWT");
							} else {
								 regClient = service.getClient(clientIdFromJWTToken);
								if (regClient != null) {
									DafAuthtemp authTemp = new DafAuthtemp();
									OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
									final String accessToken = oauthIssuerImpl.accessToken();

									JSONObject jsonOb = new JSONObject();
									jsonOb.put("access_token", accessToken);
									jsonOb.put("token_type", "bearer");
									jsonOb.put("expires_in", "1800");
									jsonOb.put("scope", scope);
									response.addHeader("Content-Type", "application/json");
									String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Timestamp(System.currentTimeMillis()));
									authTemp.setClient_id(regClient.getClient_id());
									authTemp.setScope(scope);
									authTemp.setAccess_token(accessToken);
									authTemp.setExpiry(timeStamp);
									authTemp.setRefresh_token(authTemp.getRefresh_token());
									dao.saveOrUpdate(authTemp);
									out.println(jsonOb.toString());
								
								} else {
									response.sendError(401, "client id doesn't match with registerd one");
								}
							}

					}else {
						response.sendError(401, "client id doesn't match with registerd one");
					}

					} else {
						// check the signature
						JWTService jwtValidator = null;

						// otherwise load from the server's public key
						jwtValidator = validationServices.getValidator(jku.toString());
						if (jwtValidator != null) {
							if (!jwtValidator.validateSignature(signedIdToken)) {
								response.sendError(401, "public key could not validate the JWT");
							} else {
							 regClient = service.getClient(clientIdFromJWTToken);
								if (regClient != null) {
									DafAuthtemp authTemp = new DafAuthtemp();
									OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
									final String accessToken = oauthIssuerImpl.accessToken();

									JSONObject jsonOb = new JSONObject();
									jsonOb.put("access_token", accessToken);
									jsonOb.put("token_type", "bearer");
									jsonOb.put("expires_in", "1800");
									jsonOb.put("scope", scope);
									response.addHeader("Content-Type", "application/json");
									String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Timestamp(System.currentTimeMillis()));
									authTemp.setClient_id(regClient.getClient_id());
									authTemp.setScope(scope);
									authTemp.setAccess_token(accessToken);
									authTemp.setExpiry(timeStamp);
									authTemp.setRefresh_token(authTemp.getRefresh_token());
									dao.saveOrUpdate(authTemp);
									out.println(jsonOb.toString());

								} else {
									response.sendError(401, "client id doesn't match with registerd one");
								}
							}

						} else {
							response.sendError(401, "JKU Url is invalid");
							}

						}

					} else {
						response.sendError(400, "JSON Web Token is invalid");
					}

				}
			} else {
				response.sendError(400, "content type or accept headers is incorrect");
			}
		} else {
			response.sendError(400, "incorrect headers ");
		}
	}
}
