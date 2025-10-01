package com.example.scheduler.domain.exception;

import java.time.Instant;

public class ApiError {
    Instant timestamp;
    Integer status;
    String error;
    String message;
    String path;

    public ApiError(Instant timestamp,
                    int status,
                    String error,
                    String message,
                    String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}