package com.chiacademy.phonecontacts.exception.exception;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException() {
        super("You don't have sufficient rights to perform this action");
    }
}
