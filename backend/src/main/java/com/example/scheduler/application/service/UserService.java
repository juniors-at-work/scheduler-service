package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.AuthResponse;
import com.example.scheduler.adapters.dto.user.RegisterRequest;

public interface UserService {
    AuthResponse registerUser(RegisterRequest request);

    AuthResponse loginUser(String username, String password);
}