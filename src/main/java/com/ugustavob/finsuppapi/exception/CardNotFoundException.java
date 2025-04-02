package com.ugustavob.finsuppapi.exception;

public class CardNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Card not found";

    public CardNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CardNotFoundException(String message) {
        super(message);
    }
}
