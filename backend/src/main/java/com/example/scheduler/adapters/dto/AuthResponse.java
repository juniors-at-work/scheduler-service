package com.example.scheduler.adapters.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}