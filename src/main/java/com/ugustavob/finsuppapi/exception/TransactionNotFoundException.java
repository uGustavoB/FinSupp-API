package com.ugustavob.finsuppapi.exception;

public class TransactionNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Transaction not found";

    public TransactionNotFoundException() {
        super(defaultMessage);
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
