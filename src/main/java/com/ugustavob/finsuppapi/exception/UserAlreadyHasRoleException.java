package com.ugustavob.finsuppapi.exception;

public class UserAlreadyHasRoleException extends RuntimeException{
    public UserAlreadyHasRoleException() {
        super("User already has this role");
    }

    public UserAlreadyHasRoleException(String message) {
        super(message);
    }
}
