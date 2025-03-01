package com.ugustavob.finsuppapi.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String defaultMessage = "User already exists";

    public UserAlreadyExistsException() {
        super(defaultMessage);
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
