package com.ugustavob.finsuppapi.exceptions;

public class AccountAlreadyExistsException extends RuntimeException {
    private static final String defaultMessage = "Account already exists! Please, try again with a different " +
            "description.";

    public AccountAlreadyExistsException() { super(defaultMessage); }

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
