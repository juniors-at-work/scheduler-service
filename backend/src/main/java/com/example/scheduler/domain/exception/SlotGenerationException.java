package com.example.scheduler.domain.exception;

public class SlotGenerationException extends RuntimeException {
    public SlotGenerationException(String message) {
        super(message);
    }
}
