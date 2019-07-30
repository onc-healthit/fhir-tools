package org.sitenv.spring.exception;

public class ClientNotFoundException extends Exception {

	
	private static final long serialVersionUID = 1L;
		String customMessage;

		   public ClientNotFoundException(String message) {
			   customMessage = message;
		   }

		   public String toString() {
		      return customMessage;
		   }
		}

