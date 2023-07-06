package com.chiacademy.phonecontacts.exception.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("User with such credentials is not found");
    }
}
