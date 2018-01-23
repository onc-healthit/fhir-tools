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

import org.sitenv.spring.configuration.AppConfig;
import org.sitenv.spring.model.DafQuestionnaire;
import org.sitenv.spring.service.QuestionnaireService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

@WebServlet(urlPatterns = {"/fhir/Questionnaire/*"}, displayName = "FHIR Server Meatdata")
public class QuestionnaireResource extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AbstractApplicationContext context;
	QuestionnaireService service;
	
	

    public QuestionnaireResource() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        service = (QuestionnaireService) context.getBean("questionnaireService");
    }
	
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 PrintWriter out = response.getWriter();
		 String pathInfo = request.getPathInfo();
		 
		 if(pathInfo!=null && !pathInfo.equals("/")){
			 try{
				 Integer id = Integer.parseInt(pathInfo.replace("/", ""));
				 String quest = service.getQuestionnaireById(id).getQuestionnaire();
				 response.setContentType("application/json");
			        out.println(quest);
			 } catch (Exception e) {
					/*
					 * If we can't parse the ID as a long, it's not valid so this is an unknown resource
					 */
				 	response.setStatus(401);
			        out.println("ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException:+Questionnaire"+pathInfo);
			    }
		 }else{
			 List<String> stringsList = new ArrayList<String>();
			 List<DafQuestionnaire> qrList = service.getAllQuestionnaires();
			 for(DafQuestionnaire qr : qrList){
				 stringsList.add(qr.getQuestionnaire());
			 }
			 //JSONObject json = new JSONObject(stringsList);
			 response.setContentType("application/json");
			 out.println(stringsList);
		 }
	 }
	 
	 public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
	 {
		 String pathInfo = req.getPathInfo();
		 PrintWriter out = res.getWriter();
		 if(pathInfo == null || pathInfo.equals("/")){
		 
		 DafQuestionnaire dafQuestionnaire = new DafQuestionnaire();
		 dafQuestionnaire.setQuestionnaire(getBody(req));
		
		 Integer genId = service.saveQuestionnaire(dafQuestionnaire);
		 res.setStatus(201);
		 out.println("Questionnaire/"+genId);
		 }else{
		 res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Method not allowed");
		 }
		 
	 }
	 
	 public static String getBody(HttpServletRequest request) throws IOException {

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
