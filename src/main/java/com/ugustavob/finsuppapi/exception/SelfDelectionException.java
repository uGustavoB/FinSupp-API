package com.ugustavob.finsuppapi.exception;

public class SelfDelectionException extends RuntimeException {
    private static final String defaultMessage = "You can't delete yourself";

    public SelfDelectionException() {
        super(defaultMessage);
    }

    public SelfDelectionException(String message) {
        super(message);
    }
}
