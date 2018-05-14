package org.sitenv.spring;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.sitenv.spring.model.DataSource;
import org.sitenv.spring.model.ExtractionTask;
import org.sitenv.spring.service.DataSourceService;
import org.sitenv.spring.service.ExtractionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Controller
@RequestMapping("")
public class BulkDataRequestClient {

	@Autowired
	private ExtractionTaskService etService;

	@Autowired
	private DataSourceService dsService;

	@RequestMapping(value = "fhir/Patient/$export", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String getResourceValues(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String queryString = request.getQueryString();

		String dsId = request.getHeader("dsId");

		if (dsId != null) {
			DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
			String serverBase = ds.getFhirURL();
			Boolean mode = ds.getIsSecure();

			String queryURL = request.getRequestURI().substring(request.getContextPath().length()).split("/", 3)[2];
			String url = serverBase + "/" + queryURL;
			if (queryString != null) {
				url = url + "?" + queryString;
			}

			String accessToken = null;
			if (mode != null && mode) {
				accessToken = getAccessToken(ds, request);
			}

			String retVal = invokeURL(url, accessToken, request, response);

			String contentLocation = response.getHeader("Content-Location");
			if (contentLocation != null) {
				String uri = request.getScheme() + "://" + request.getServerName()
						+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
								|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
										: ":" + request.getServerPort())
						+ request.getContextPath();

				if (response.getStatus() == 200) {
					response.setStatus(202);
				}

				// save Extraction task details
				ExtractionTask et = new ExtractionTask();
				et.setContentLocation(contentLocation);
				et.setRequestURL(url);
				et.setResponseBody(retVal);
				et.setStatus("Pending");
				et.setProcessFlag(false);
				et.setAuthenticationMode(mode.toString());

				etService.saveOrUpdate(et);

				response.setHeader("Content-Location", uri + "/bulkdata/" + et.getExtractionTaskId());
			}

			return retVal;

		} else {

			throw new Exception("Invalid DataSource Id...!");
		}

	}

	@RequestMapping(value = "fhir/Group/{id}/$export", method = RequestMethod.GET)
	@ResponseBody
	public String getGroupBulkData(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String queryString = request.getQueryString();

		String dsId = request.getHeader("dsId");
		if (dsId != null) {
			DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
			String serverBase = ds.getFhirURL();
			Boolean mode = ds.getIsSecure();
			String queryURL = request.getRequestURI().substring(request.getContextPath().length()).split("/", 3)[2];
			String url = serverBase + "/" + queryURL;
			if (queryString != null) {
				url = url + "?" + queryString;
			}

			String accessToken = null;
			if (mode != null && mode) {
				accessToken = getAccessToken(ds, request);
			}
			String retVal = invokeURL(url, accessToken, request, response);

			String contentLocation = response.getHeader("Content-Location");
			if (contentLocation != null) {
				String uri = request.getScheme() + "://" + request.getServerName()
						+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
								|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
										: ":" + request.getServerPort())
						+ request.getContextPath();

				if (response.getStatus() == 200) {
					response.setStatus(202);
				}

				ExtractionTask et = new ExtractionTask();
				et.setGroupId(id);
				et.setContentLocation(contentLocation);
				et.setRequestURL(url);
				et.setResponseBody(retVal);
				et.setStatus("Pending");
				et.setProcessFlag(false);
				et.setAuthenticationMode(mode.toString());

				etService.saveOrUpdate(et);

				response.setHeader("Content-Location", uri + "/bulkdata/" + et.getExtractionTaskId());
			}
			return retVal;
		} else {
			throw new Exception("Invalid DataSource Id...!");
		}
	}

	@RequestMapping(value = "fhir/Group", method = RequestMethod.POST)
	@ResponseBody
	public String postGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody String payload)
			throws Exception {

		String dsId = request.getHeader("dsId");
		if (dsId != null) {
			DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
			String serverBase = ds.getFhirURL();
			Boolean mode = ds.getIsSecure();

			String queryURL = request.getRequestURI().substring(request.getContextPath().length()).split("/", 3)[2];
			String url = serverBase + "/" + queryURL;
			;

			String accessToken = null;

			StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost requestPost = new HttpPost(url);
			requestPost.setEntity(entity);
			if (mode != null && mode) {
				accessToken = getAccessToken(ds, request);
				requestPost.addHeader("Authorization", "bearer " + accessToken);
			}

			HttpResponse httpResponse = httpClient.execute(requestPost);
			response.setStatus(httpResponse.getStatusLine().getStatusCode());
			response.setContentType("application/json+fhir;charset=UTF-8");
			String json = EntityUtils.toString(httpResponse.getEntity());
			return json;
		} else {
			throw new Exception("Invalid DataSource Id...!");
		}

	}

	@RequestMapping(value = "bulkdata/{etId}", method = RequestMethod.GET)
	@ResponseBody
	public String getContentLocationResponse(@PathVariable Integer etId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ExtractionTask et = etService.getExtractionTaskById(etId);
		if (et != null && et.getContentLocation() != null) {

			String dsId = request.getHeader("dsId");
			if (dsId != null) {
				DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
				Boolean mode = ds.getIsSecure();
				String url = et.getContentLocation();
				String clientUri = request.getScheme() + "://" + request.getServerName()
						+ ("http".equals(request.getScheme()) && request.getServerPort() == 80
								|| "https".equals(request.getScheme()) && request.getServerPort() == 443 ? ""
										: ":" + request.getServerPort())
						+ request.getContextPath();

				String accessToken = null;
				if (mode != null && mode) {
					accessToken = getAccessToken(ds, request);
				}
				String retVal = invokeURL(url, accessToken, request, response);

				String linkHeader = response.getHeader("Link");
				if (linkHeader != null) {
					String[] splitList = linkHeader.split(",");

					StringBuilder customLinks = new StringBuilder();
					for (int i = 0; i < splitList.length; i++) {
						String[] linksList = splitList[i].replaceAll("[<>]", "").split("/");
						customLinks.append("<" + clientUri + "/bulkdata/download/" + linksList[linksList.length - 2]
								+ "/" + linksList[linksList.length - 1] + ">");

						if (i < splitList.length - 1) {
							customLinks.append(",");
						}

					}
					response.setHeader("Link", customLinks.toString());
				}
				return retVal;
			} else {
				response.setStatus(404);
				return "Requested URL is not valid";
			}

		} else {
			response.setStatus(404);
			return "Content Location not found ...!";
		}

	}

	@RequestMapping(value = "fhir/{resource}", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String getResource(@PathVariable String resource, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String dsId = request.getHeader("dsId");
		if (dsId != null) {
			DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
			String serverBase = ds.getFhirURL();
			Boolean mode = ds.getIsSecure();

			String queryURL = request.getRequestURI().substring(request.getContextPath().length()).split("/", 3)[2];
			String url = serverBase + "/" + queryURL;
			String queryString = request.getQueryString();
			if (queryString != null) {
				url = url + "?" + queryString;
			}

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet bulkRequest = new HttpGet(url);
			String accessToken = null;
			if (mode != null && mode) {
				accessToken = getAccessToken(ds, request);
			}
			// add request header
			bulkRequest.addHeader("Prefer", "respond-async");
			if (accessToken != null)
				bulkRequest.addHeader("Authorization", "bearer " + accessToken);

			HttpResponse bulkResponse = client.execute(bulkRequest);

			Header[] headers = bulkResponse.getAllHeaders();
			for (Header header : headers) {
				if (!header.getName().equalsIgnoreCase("Transfer-Encoding")) {
					response.setHeader(header.getName(), header.getValue());
				}
			}

			response.setContentType("application/json+fhir;charset=UTF-8");
			String json = EntityUtils.toString(bulkResponse.getEntity());

			return json;

		} else {
			throw new Exception("Invalid DataSource Id...!");
		}

	}

	@RequestMapping(value = "bulkdata/load/request/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void loadRequestById(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String dsId = request.getHeader("dsId");
		if (dsId != null) {
			DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
			String serverBase = ds.getFhirURL();
			String url = serverBase + request.getRequestURI().substring(request.getContextPath().length());
			response.sendRedirect(url);
		}
	}

	public String invokeURL(String url, String accessToken, HttpServletRequest request, HttpServletResponse response)
			throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet bulkRequest = new HttpGet(url);

		// add request header
		bulkRequest.addHeader("Prefer", "respond-async");
		bulkRequest.addHeader("Accept", "application/fhir+json");
		if (accessToken != null) {
			bulkRequest.addHeader("Authorization", "bearer " + accessToken);
		}

		HttpResponse bulkResponse = client.execute(bulkRequest);

		Header[] headers = bulkResponse.getAllHeaders();
		for (Header header : headers) {
			if (!header.getName().equalsIgnoreCase("Transfer-Encoding")) {
				response.setHeader(header.getName(), header.getValue());
			}
		}
		response.setStatus(bulkResponse.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(bulkResponse.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		response.setContentType("application/xml+fhir;charset=UTF-8");

		return result.toString();
	}

	@RequestMapping(value = "bulkdata/download/{id}/{fileName:.+}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadFile(@PathVariable Integer id, @PathVariable String fileName, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String dsId = request.getHeader("dsId");
		if (dsId != null) {
			DataSource ds = dsService.getDataSourceById(Integer.parseInt(dsId));
			String serverBase = ds.getFhirURL();

			String url = serverBase + request.getRequestURI().substring(request.getContextPath().length());

			response.sendRedirect(url);
			response.setStatus(302);
		}

	}

	public String getAccessToken(DataSource ds, HttpServletRequest request) throws Exception {
		try {
			String accessToken = null;
			String aud = ds.getAud();

			String client_assertion = getCompactJWS(ds);
			String json = "grant_type=client_credentials&scope=" + ds.getScope()
					+ "&client_assertion_type=urn:ietf:params:oauth:client-assertion-type:jwt-bearer&client_assertion="
					+ client_assertion;
			StringEntity entity = new StringEntity(json, ContentType.DEFAULT_TEXT);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost requestPost = new HttpPost(aud);
			requestPost.setEntity(entity);

			HttpResponse response = httpClient.execute(requestPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				JSONObject payLoad = new JSONObject(result.toString());

				accessToken = payLoad.getString("access_token");
				if (request != null) {
					HttpSession httpSession = request.getSession();
					HashMap<String, String> sessionMap = new HashMap<String, String>();
					sessionMap.put("access_token", accessToken);
					httpSession.setAttribute("access_token", accessToken);
				}
			}

			return accessToken;
		} catch (Exception e) {
			System.out.println("error :" + e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	public String getCompactJWS(DataSource ds) throws Exception {

		String issuer = ds.getIss();
		String keypath = ds.getKeyLocation();
		String subject = ds.getSub();
		String aud = ds.getAud();

		File f = new File(keypath);
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		Key key = KeyFactory.getInstance("RSA").generatePrivate(spec);
		String compactJws = Jwts.builder().setSubject(subject).setIssuedAt(new Date()).setIssuer(issuer)
				.setAudience(aud).signWith(SignatureAlgorithm.RS256, key).compact();

		return compactJws;

	}

}
