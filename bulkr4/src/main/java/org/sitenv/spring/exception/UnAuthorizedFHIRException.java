package org.sitenv.spring.exception;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

public class UnAuthorizedFHIRException extends BaseServerResponseException {
	private static final String DEFAULT_MESSAGE = "Unauthorized Exception";
	private static final long serialVersionUID = 1L;
	public static final int STATUS_CODE = Constants.STATUS_HTTP_401_CLIENT_UNAUTHORIZED;

	public UnAuthorizedFHIRException(String theMessage) {
		super(STATUS_CODE, theMessage);
	}

	UnAuthorizedFHIRException(int message, Throwable throwable) {
		super(message, throwable);
	}

	UnAuthorizedFHIRException(Throwable throwable) {
		super(STATUS_CODE, throwable);
	}

	public String getMessage() {
		return super.getMessage();
	}

}