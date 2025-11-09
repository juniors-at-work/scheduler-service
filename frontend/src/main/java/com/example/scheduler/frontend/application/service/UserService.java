package com.example.scheduler.frontend.application.service;

import com.example.scheduler.frontend.adapter.dto.RegisterUserRequest;
import org.springframework.ui.Model;

public interface UserService {
    void registerUser(RegisterUserRequest request, Model model);
}
