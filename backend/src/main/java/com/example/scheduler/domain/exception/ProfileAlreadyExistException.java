package com.example.scheduler.domain.exception;

public class ProfileAlreadyExistException extends RuntimeException {

    public ProfileAlreadyExistException(String message) {
        super(message);
    }
}
