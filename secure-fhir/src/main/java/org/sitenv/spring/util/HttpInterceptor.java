package org.sitenv.spring.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.TagList;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;

public class HttpInterceptor implements IServerInterceptor {
	
	public void interceptRequest(IHttpRequest req) {
		 
		// Add return format
		req.addHeader("Content-Type", "application/json+fhir");
		
		// Add Accept header
	}

	public void interceptResponse(IHttpResponse resp) throws IOException {
		
	}

	@Override
	public boolean handleException(RequestDetails arg0, BaseServerResponseException arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws ServletException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean incomingRequestPostProcessed(RequestDetails arg0, HttpServletRequest arg1, HttpServletResponse arg2)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void incomingRequestPreHandled(RestOperationTypeEnum arg0, ActionRequestDetails arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean incomingRequestPreProcessed(HttpServletRequest arg0, HttpServletResponse arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, IBaseResource arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, TagList arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, HttpServletRequest arg1, HttpServletResponse arg2)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, Bundle arg1, HttpServletRequest arg2, HttpServletResponse arg3)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, IBaseResource arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws AuthenticationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean outgoingResponse(RequestDetails arg0, TagList arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws AuthenticationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BaseServerResponseException preProcessOutgoingException(RequestDetails arg0, Throwable arg1,
			HttpServletRequest arg2) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

}
