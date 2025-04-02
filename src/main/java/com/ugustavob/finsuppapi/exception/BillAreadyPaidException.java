package com.ugustavob.finsuppapi.exception;

public class BillAreadyPaidException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Bill already paid";

    public BillAreadyPaidException() {
        super(DEFAULT_MESSAGE);
    }

    public BillAreadyPaidException(String message) {
        super(message);
    }
}
