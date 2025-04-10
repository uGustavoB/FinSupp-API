package com.ugustavob.finsuppapi.exception;

public class AccountCannotBeDeletedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Account cannot be deleted because it has transactions or subscriptions associated with it.";

    public AccountCannotBeDeletedException(String message) {
        super(message);
    }

    public AccountCannotBeDeletedException() {
        super(DEFAULT_MESSAGE);
    }
}
