package com.ugustavob.finsuppapi.exception;

public class CategoryNotFoundException extends RuntimeException {
    private static final String defaultMessage = "Category not found";

    public CategoryNotFoundException() {
        super(defaultMessage);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
