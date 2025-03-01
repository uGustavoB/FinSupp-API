package com.ugustavob.finsuppapi.exception;

public class AccountNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Account not found";

    public AccountNotFoundException() {
        super(defaultMessage);
    }

    public AccountNotFoundException(String message) {
        super(message);
    }
}
