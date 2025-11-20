package com.example.scheduler.frontend.adapter.web;

import com.example.scheduler.frontend.adapter.dto.RegisterUserRequest;
import com.example.scheduler.frontend.application.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/register")
    public String getRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(RegisterUserRequest request, Model model) {
        service.registerUser(request, model);
        return "register";
    }
}
