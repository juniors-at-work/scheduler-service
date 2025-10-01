package com.example.scheduler.domain.exception;

public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String token) {
        super(String.format("Failed refresh token [%s]", token));
    }
}
