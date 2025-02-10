package com.ugustavob.finsuppapi.exceptions;

public class UserNotFoundException extends RuntimeException {
    private static final String defaultMessage = "User not found";

    public UserNotFoundException() {
        super(defaultMessage);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
