package com.ugustavob.finsuppapi.exception;

public class CategoryDescriptionAlreadyExistsException extends RuntimeException {
    private static final String defaultMessage = "Category description already exists";

    public CategoryDescriptionAlreadyExistsException() {
        super(defaultMessage);
    }

    public CategoryDescriptionAlreadyExistsException(String categoryDescription) {
        super("Category description already exists: " + categoryDescription);
    }
}
