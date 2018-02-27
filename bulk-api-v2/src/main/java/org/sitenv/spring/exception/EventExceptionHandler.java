package org.sitenv.spring.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EventExceptionHandler extends ResponseEntityExceptionHandler{
	
	public EventExceptionHandler() {
        super();
    }
	
	 @ExceptionHandler({ FHIRHapiException.class})
		public ResponseEntity<Object> handleExceptionInternal(final FHIRHapiException de, final WebRequest request){
			final String bodyOfResponse = "Client Name is already existed. Please use a different Client Name";
			return handleExceptionInternal(de,bodyOfResponse,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
		}

}
