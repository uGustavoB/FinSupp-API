package com.ugustavob.finsuppapi.exception;

public class CardAlreadyExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Card already exists";

    public CardAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public CardAlreadyExistsException(String message) {
        super(message);
    }
}
