package org.sitenv.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafQuestionnaireResponse;
import org.sitenv.spring.service.QuestionnaireResponseService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

@WebServlet(urlPatterns = {"/fhir/QuestionnaireResponse/*"}, displayName = "FHIR Server Meatdata")
public class QuestionnaireResponseResource extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AbstractApplicationContext context;
	QuestionnaireResponseService service;
	
	

    public QuestionnaireResponseResource() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (QuestionnaireResponseService) context.getBean("questionnaireResponseService");
    }
	
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 PrintWriter out = response.getWriter();
		 String pathInfo = request.getPathInfo();
		 
		 String questionnaireId= request.getParameter("questionnaire");
		 if(questionnaireId != null){
			 try{
			 List<String> stringList = new ArrayList<String>();
			 List<DafQuestionnaireResponse> qrList = service.getQuestionnaireResponsesForQuestionnaire(Integer.parseInt(questionnaireId));
			 for(DafQuestionnaireResponse qr : qrList){
				 stringList.add(qr.getQuestionnaire_response());
			 }
			 response.setContentType("application/json");
			 out.println(stringList);
			 }catch(Exception e){
				 response.sendError(HttpServletResponse.SC_BAD_REQUEST, questionnaireId +" is not valid");
			 }
			 
		 }else if(pathInfo!=null && !pathInfo.equals("/") && questionnaireId == null){
			 try{
				 String id = pathInfo.replace("/", "");
				// JSONObject json = new JSONObject(service.getQuestionnaireResponseById(id).getQuestionnaire_response());
				 String qResp = service.getQuestionnaireResponseById(id).getQuestionnaire_response(); 
				 response.setContentType("application/json");
			        out.println(qResp);
			 } catch (Exception e) {
					/*
					 * If we can't parse the ID as a long, it's not valid so this is an unknown resource
					 */
				 	response.setStatus(404);
				 	response.sendError(HttpServletResponse.SC_NOT_FOUND, "QuestionnaireResponse"+pathInfo +"not found");
			    }
		 }else{
			 List<String> stringsList = new ArrayList<String>();
			 List<DafQuestionnaireResponse> qrList = service.getAllQuestionnaireResponses();
			 for(DafQuestionnaireResponse qr : qrList){
				 stringsList.add(qr.getQuestionnaire_response());
			 }
			// JSONObject json = new JSONObject(stringsList);
			 response.setContentType("application/json");
			 out.println(stringsList);
		 }
	 }
	 
	 public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
	 {
		 PrintWriter out = res.getWriter();
		 String pathInfo = req.getPathInfo();
		 if(pathInfo == null || pathInfo.equals("/")){
		 DafQuestionnaireResponse dafQuestionnaireResponse = new DafQuestionnaireResponse();
		 String requestBody = getRequestBody(req);
		 dafQuestionnaireResponse.setQuestionnaire_response(requestBody);
		 
		 JSONObject json = new JSONObject(requestBody);
		// q.setId(json.getInt("questionnaire"));
		 JSONObject ob = json.getJSONObject("questionnaire");//("questionnaire").toString().split("/")[1].replaceAll("[^a-zA-Z0-9]","");
		 String stringQuestionnaireId = ob.getString("reference").split("/")[1].replaceAll("[^a-zA-Z0-9]","");
		 
		 String responseId = json.getString("id");
		 try{
		 dafQuestionnaireResponse.setQuestionnaire_id(Integer.parseInt(stringQuestionnaireId));
		 dafQuestionnaireResponse.setResponse_id(responseId);
		 }catch(Exception e){
			 //throw new 
		 }
		 Integer genId = service.saveQuestionnaire(dafQuestionnaireResponse);
		 res.setStatus(201);
		 res.setContentType("application/json");
		 out.println("QuestionnaireResponse/"+genId);
	 	}else{
		 res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Method not allowed");
		 }
		 
	 }
	 
	 public static String getRequestBody(HttpServletRequest request) throws IOException {

		    String body = null;
		    StringBuilder stringBuilder = new StringBuilder();
		    BufferedReader bufferedReader = null;

		    try {
		        InputStream inputStream = request.getInputStream();
		        if (inputStream != null) {
		            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		            char[] charBuffer = new char[128];
		            int bytesRead = -1;
		            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		                stringBuilder.append(charBuffer, 0, bytesRead);
		            }
		        } else {
		            stringBuilder.append("");
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

		    body = stringBuilder.toString();
		    return body;
		}


}
