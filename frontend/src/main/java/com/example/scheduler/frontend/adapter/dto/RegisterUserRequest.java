package com.example.scheduler.frontend.adapter.dto;

public record RegisterUserRequest(String email, String username, String password, String confirmPassword) {
}
