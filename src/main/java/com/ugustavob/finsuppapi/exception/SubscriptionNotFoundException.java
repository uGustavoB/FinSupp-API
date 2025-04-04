package com.ugustavob.finsuppapi.exception;

public class SubscriptionNotFoundException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Subscription not found";

    public SubscriptionNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}
