package com.ugustavob.finsuppapi.exception;

public class InvalidCredentialsException extends RuntimeException{
    private static final String defaultMessage = "Invalid credentials";

    public InvalidCredentialsException() {
        super(defaultMessage);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
