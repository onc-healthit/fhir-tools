package org.sitenv.spring.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONObject;
import org.sitenv.spring.model.DafAuthtemp;
import org.sitenv.spring.model.DafClientRegister;
import org.sitenv.spring.model.DafUserRegister;
import org.sitenv.spring.service.AuthTempService;
import org.sitenv.spring.service.ClientRegistrationService;
import org.sitenv.spring.service.UserRegistrationService;
import org.sitenv.spring.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/token")
public class TokenEndPoint extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int HOUR = 3600 * 1000; // in milli-seconds.
	public static final String SMART_STYLE_URL = "view/css/smart_v1.json";

	Logger log = (Logger) LoggerFactory.getLogger(TokenEndPoint.class);

	@Autowired
	private ClientRegistrationService service;

	@Autowired
	private UserRegistrationService userService;

	@Autowired
	private AuthTempService authTempService;

	@Autowired
	private JwtGenerator jwtGenerator;

	/** generates access token and other data based on the scopes 
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public void getAuthorization(HttpServletRequest request, HttpServletResponse response) throws Exception {

		PrintWriter out = response.getWriter();

		log.info("Token url:" + request.getRequestURL().append('?').append(request.getQueryString()));

		String credentials = request.getHeader("Authorization");
		String client_id = null;
		String client_secret = null;
		String code = null;
		String token = request.getParameter("refresh_token");
		String grant_type = null;
		if (credentials != null) {
			credentials = credentials.substring(6);
			log.info(credentials);
			String cred = CommonUtil.base64Decoder(credentials);
			String[] credList = cred.split(":", 2);
			log.info(credList[0]);
			log.info(credList[1]);
			client_id = credList[0];
			client_secret = credList[1];
			code = request.getParameter("code");
			grant_type = request.getParameter("grant_type");
		} else if (request.getParameter("client_id") != null) { 			
			client_id = request.getParameter("client_id");
			client_secret = request.getParameter("client_secret");
			
			code = request.getParameter("code");
			grant_type = request.getParameter("grant_type");
		}  else {
		
			StringBuffer sb = new StringBuffer();
			BufferedReader bufferedReader = null;

			try {
				bufferedReader = request.getReader(); // new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead;
				while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
					sb.append(charBuffer, 0, bytesRead);
				}

			} catch (IOException ex) {
				throw ex;
			} finally {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException ex) {
						throw ex;
					}
				}
			}

			String jsonString;
			if (!sb.toString().isEmpty() && sb.toString() != null) {
				if (sb.toString().contains("{") && sb.toString().contains("}")) {
					jsonString = sb.toString().replace("=", ":").replace("&", ",");
				} else {
					jsonString = "{" + sb.toString().replace("=", ":").replace("&", ",") + "}";
				}

				log.info("body" + jsonString);

				JSONObject payLoad = new JSONObject(jsonString);
				client_id = payLoad.getString("client_id");
				client_secret = payLoad.getString("client_secret");
				code = payLoad.getString("code");
				grant_type = payLoad.getString("grant_type");
			}
		}

		log.info("client id : " + client_id);
		log.info("Client_secret: " + client_secret);
		log.info("code:" + code);
		log.info("grant_type:" + grant_type);
		if (client_id == null) {
			response.sendError(401, "client_id is not present");
		} else if (code == null && token == null) {
			response.sendError(401, "code or refresh_token is not present");
		} else if (grant_type == null) {
			response.sendError(401, "grant_type is not present");
		}else if(service.getClient(client_id) == null) {
			response.sendError(401, "Invalid client_id");
		}else if ((client_secret != null) && (client_id != null)) {
			//verifying client_id and client_secret
		
			DafClientRegister client = service.getClientByCredentials(client_id, client_secret);
			if (client != null) {

				Integer userId = client.getUserId();
				DafUserRegister user = userService.getUserById(userId);

				DafAuthtemp authTemp = authTempService.getAuthById(client_id);

				if (authTemp != null) {
					List<String> scopes = Arrays.asList(authTemp.getScope().split(","));
					StringBuilder stringScope = new StringBuilder();
					int scopesint = scopes.size();
					for (int i = 0; i < scopesint; i++) {
						stringScope.append(scopes.get(i));
						if (i < scopesint) {
							stringScope.append(" ");
						}
					}
					stringScope.toString();

					try {
						OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
						if (grant_type.equals(GrantType.AUTHORIZATION_CODE.toString())) {
							if (!code.equalsIgnoreCase(authTemp.getAuthCode())) {
								response.sendError(400, "Invalid authorization code");
								out.println(response);
							} else if (!client.getClient_id().equals(client_id)) {
								response.sendError(401, "Invalid Client ID");
								out.println(response);
							} else {
								final String accessToken = oauthIssuerImpl.accessToken();
								// org.json.JSONObject jsonHash = new org.json.JSONObject(new HashMap<K, V>());

								JSONObject jsonOb = new JSONObject();
								jsonOb.put("access_token", accessToken);
								jsonOb.put("token_type", "bearer");
								jsonOb.put("expires_in", 3600);
								jsonOb.put("scope", stringScope.toString());

								String refreshToken = null;
								String idToken = null;
								String fhirUser = "";
								if (scopes.contains("launch/patient") || scopes.contains("launch")) {
									fhirUser=authTemp.getLaunchPatientId();
									jsonOb.put("patient", String.valueOf(authTemp.getLaunchPatientId()));
									jsonOb.put("need_patient_banner", true);
									jsonOb.put("smart_style_url", Common.getBaseUrl(request)+SMART_STYLE_URL);
								}
								if (scopes.contains("offline_access") || scopes.contains("online_access")) {
									refreshToken = oauthIssuerImpl.refreshToken();
									jsonOb.put("refresh_token", refreshToken);
								}
								if (scopes.contains("openid")) {
									String sub = user.getUser_name();
									String aud = client.getClient_id();
									String email = user.getUser_email();
									String userName = user.getUser_name();
									String timeStamp = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy")
											.format(new Timestamp(System.currentTimeMillis()));
									Date issueDate = Common.convertToDateFormat(timeStamp);
									Date expiryTime = new Date(issueDate.getTime() + 2 * HOUR);
									Map<String, Object> payloadData = new HashMap<>();
									payloadData.put("sub", sub);
									payloadData.put("aud", aud);
									payloadData.put("email", email);
									payloadData.put("issueDate", issueDate);
									payloadData.put("expiryTime", expiryTime);
									payloadData.put("userName", userName);
									payloadData.put("fhirUser", fhirUser);
									

									idToken = jwtGenerator.generate(payloadData, request);
									jsonOb.put("id_token", idToken);
								}
								response.addHeader("Content-Type", "application/json");
								response.addHeader("Cache-Control", "no-store");
								response.addHeader("Pragma", "no-cache");
								

								String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Timestamp(System.currentTimeMillis()));
								// hm1.put(accessToken, timeStamp);
								authTemp.setClient_secret(client_secret);
								authTemp.setAccess_token(accessToken);
								authTemp.setExpiry(timeStamp);
								authTemp.setRefresh_token(refreshToken);
								authTemp.setIdToken(idToken);

								authTempService.saveOrUpdate(authTemp);

								out.println(jsonOb.toString());
							}
						} else if (grant_type.equals(GrantType.REFRESH_TOKEN.toString())) {
							
							if (authTemp.getRefresh_token() != null && authTemp.getRefresh_token().equals(token)) {
								HttpSession session = request.getSession();
								HashMap<String, Integer> sessionObj = (HashMap<String, Integer>) session
										.getAttribute("user" + client.getUserId());
								Integer currentTime = (int) (System.currentTimeMillis() / 1000L);
								if (scopes.contains("online_access") && sessionObj != null
										&& currentTime > sessionObj.get("expiry")) {
									response.sendError(401, "Invalid refresh_token");
									out.println(response);
								} else if (!client.getClient_id().equals(client_id)) {
									response.sendError(401, "Invalid Client ID");
									out.println(response);
							} else {
									
									final String accessToken = oauthIssuerImpl.accessToken();
									final String refreshToken =oauthIssuerImpl.refreshToken();
									JSONObject jsonOb = new JSONObject();
									jsonOb.put("access_token", accessToken);
									jsonOb.put("patient", String.valueOf(authTemp.getLaunchPatientId()));
									jsonOb.put("token_type", "bearer");
									jsonOb.put("expires_in", 3600); // 3600 second is 1hour
									jsonOb.put("scope", stringScope.toString());
									jsonOb.put("refresh_token", refreshToken);

									// String refreshToken = null;
									if (scopes.contains("launch/patient")) {
										jsonOb.put("patient", String.valueOf(authTemp.getLaunchPatientId()));
									}
									/*
									 * if(scopes.contains("offline_access")){ refreshToken =
									 * oauthIssuerImpl.refreshToken(); jsonOb.put("refresh_token", refreshToken);
									 * 
									 * }else if(scopes.contains("online_access")){ refreshToken =
									 * oauthIssuerImpl.refreshToken(); jsonOb.put("refresh_token", refreshToken); }
									 */

									response.addHeader("Content-Type", "application/json");
									response.addHeader("Cache-Control", "no-store");
									response.addHeader("Pragma", "no-cache");
									String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Timestamp(System.currentTimeMillis()));
									authTemp.setClient_secret(client_secret);
									authTemp.setAccess_token(accessToken);
									authTemp.setExpiry(timeStamp);
									authTemp.setRefresh_token(refreshToken);

									authTempService.saveOrUpdate(authTemp);

									out.println(jsonOb.toString());
								}
							} else {
								response.sendError(401, "Invalid refresh_token");
							}
						}

					} catch (OAuthSystemException e) {
						e.printStackTrace();
					}

				} else {
					// the auth codes don't match.
					response.sendError(401, "Invalid authentication");
				}
			} else {
				response.sendError(401, "Invalid client_secret");
		}
	} else {
			//verifying client_id 
		
			DafClientRegister client = service.getClient(client_id);
					
				Integer userId = client.getUserId();
				DafUserRegister user = userService.getUserById(userId);

				DafAuthtemp authTemp = authTempService.getAuthById(client_id);

				if (authTemp != null) {
					List<String> scopes = Arrays.asList(authTemp.getScope().split(","));
					StringBuilder stringScope = new StringBuilder();
					int scopesint = scopes.size();
					for (int i = 0; i < scopesint; i++) {
						stringScope.append(scopes.get(i));
						if (i < scopesint) {
							stringScope.append(" ");
						}
					}
					stringScope.toString();

					try {
						OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
						if (grant_type.equals(GrantType.AUTHORIZATION_CODE.toString())) {
							if (!code.equalsIgnoreCase(authTemp.getAuthCode())) {
								response.sendError(400, "Invalid authorization code");
								out.println(response);
							} else if (!client.getClient_id().equals(client_id)) {
								response.sendError(401, "Invalid Client ID");
								out.println(response);
							} else {
								final String accessToken = oauthIssuerImpl.accessToken();
								// org.json.JSONObject jsonHash = new org.json.JSONObject(new HashMap<K, V>());

								JSONObject jsonOb = new JSONObject();
								jsonOb.put("access_token", accessToken);
								jsonOb.put("token_type", "bearer");
								jsonOb.put("expires_in", 3600);

								String refreshToken = null;
								String idToken = null;
								if (scopes.contains("launch/patient") || scopes.contains("launch")) {
									jsonOb.put("patient", String.valueOf(authTemp.getLaunchPatientId()));
								}
								if (scopes.contains("offline_access")) {
									refreshToken = oauthIssuerImpl.refreshToken();
									jsonOb.put("refresh_token", refreshToken);

								} else if (scopes.contains("online_access")) {
									refreshToken = oauthIssuerImpl.refreshToken();
									jsonOb.put("refresh_token", refreshToken);
								}
								if (scopes.contains("openid")) {
									String sub = user.getUser_name();
									String aud = client.getClient_id();
									String email = user.getUser_email();
									String userName = user.getUser_name();
									String timeStamp = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy")
											.format(new Timestamp(System.currentTimeMillis()));
									Date issueDate = Common.convertToDateFormat(timeStamp);
									Date expiryTime = new Date(issueDate.getTime() + 2 * HOUR);
									Map<String, Object> payloadData = new HashMap<>();
									payloadData.put("sub", sub);
									payloadData.put("aud", aud);
									payloadData.put("email", email);
									payloadData.put("issueDate", issueDate);
									payloadData.put("expiryTime", expiryTime);
									payloadData.put("userName", userName);

									idToken = jwtGenerator.generate(payloadData, request);
									jsonOb.put("id_token", idToken);
									jsonOb.put("scope", stringScope.toString());

								}
								response.addHeader("Content-Type", "application/json");
								response.addHeader("Cache-Control", "no-store");
								response.addHeader("Pragma", "no-cache");

								String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Timestamp(System.currentTimeMillis()));
								// hm1.put(accessToken, timeStamp);
								authTemp.setClient_secret(client_secret);
								authTemp.setAccess_token(accessToken);
								authTemp.setExpiry(timeStamp);
								authTemp.setRefresh_token(refreshToken);
								authTemp.setIdToken(idToken);

								authTempService.saveOrUpdate(authTemp);

								out.println(jsonOb.toString());
							}
						} else if (grant_type.equals(GrantType.REFRESH_TOKEN.toString())) {
							if (authTemp.getRefresh_token() != null && authTemp.getRefresh_token().equals(token)) {
								HttpSession session = request.getSession();
								HashMap<String, Integer> sessionObj = (HashMap<String, Integer>) session
										.getAttribute("user" + client.getUserId());
								Integer currentTime = (int) (System.currentTimeMillis() / 1000L);
								if (scopes.contains("online_access") && sessionObj != null
										&& currentTime > sessionObj.get("expiry")) {
									response.sendError(401, "Invalid refresh_token");
									out.println(response);
								} else if (!client.getClient_id().equals(client_id)) {
									response.sendError(401, "Invalid Client ID");
									out.println(response);
								} else {
									final String accessToken = oauthIssuerImpl.accessToken();
									final String refreshToken =oauthIssuerImpl.refreshToken();
									JSONObject jsonOb = new JSONObject();
									jsonOb.put("access_token", accessToken);
									jsonOb.put("patient", String.valueOf(authTemp.getLaunchPatientId()));
									jsonOb.put("token_type", "bearer");
									jsonOb.put("expires_in", 3600); // 3600 second is 1hour
									jsonOb.put("scope", stringScope.toString());
									jsonOb.put("refresh_token", refreshToken);

									// String refreshToken = null;
									if (scopes.contains("launch/patient")) {
										jsonOb.put("patient", String.valueOf(authTemp.getLaunchPatientId()));
									}
									/*
									 * if(scopes.contains("offline_access")){ refreshToken =
									 * oauthIssuerImpl.refreshToken(); jsonOb.put("refresh_token", refreshToken);
									 * 
									 * }else if(scopes.contains("online_access")){ refreshToken =
									 * oauthIssuerImpl.refreshToken(); jsonOb.put("refresh_token", refreshToken); }
									 */

									response.addHeader("Content-Type", "application/json");
									response.addHeader("Cache-Control", "no-store");
									response.addHeader("Pragma", "no-cache");
									String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Timestamp(System.currentTimeMillis()));
									authTemp.setClient_secret(client_secret);
									authTemp.setAccess_token(accessToken);
									authTemp.setExpiry(timeStamp);
									authTemp.setRefresh_token(refreshToken);

									authTempService.saveOrUpdate(authTemp);

									out.println(jsonOb.toString());
								}
							} else {
								response.sendError(401, "Invalid refresh_token");
							}
						}

					} catch (OAuthSystemException e) {
						e.printStackTrace();
					}

				} else {
					// the auth codes don't match.
					response.sendError(401, "Invalid authentication");
				}
				
			}
		}
	}

