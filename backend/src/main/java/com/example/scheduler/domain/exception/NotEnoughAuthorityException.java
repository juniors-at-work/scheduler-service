package com.example.scheduler.domain.exception;

public class NotEnoughAuthorityException extends RuntimeException {

    public NotEnoughAuthorityException(String message) {
        super(message);
    }
}
