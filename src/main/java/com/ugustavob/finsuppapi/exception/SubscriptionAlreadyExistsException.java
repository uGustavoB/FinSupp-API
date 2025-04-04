package com.ugustavob.finsuppapi.exception;

public class SubscriptionAlreadyExistsException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Subscription already exists for this card";

    public SubscriptionAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public SubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
