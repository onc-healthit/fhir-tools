package org.sitenv.spring.exception;

public class FHIRHapiException extends Exception {

    private static final long serialVersionUID = 1L;

    public FHIRHapiException(String message) {
        super(message);
    }

    FHIRHapiException(String message, Throwable throwable) {
        super(message, throwable);
    }

    FHIRHapiException(Throwable throwable) {
        super(throwable);
    }

    public String getMessage() {
        return super.getMessage();
    }


}
