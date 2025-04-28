package com.ugustavob.finsuppapi.exception;

public class BankNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Bank not found";

    public BankNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public BankNotFoundException(String message) {
        super(message);
    }
}
