package com.example.scheduler.frontend.application.service;

import com.example.scheduler.frontend.adapter.client.UserClient;
import com.example.scheduler.frontend.adapter.dto.RegisterUserRequest;
import com.example.scheduler.frontend.adapter.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private final UserClient client;

    public UserServiceImpl(UserClient client) {
        this.client = client;
    }

    @Override
    public void registerUser(RegisterUserRequest request, Model model) {
        if (!Objects.equals(request.password(), request.confirmPassword())) {
            model.addAttribute(ERROR, "Password and confirm password do not match");
            retainEmailAndUsername(model, request.email(), request.username());
            return;
        }
        UserDto user = new UserDto(request.email(), request.username(), request.password());
        try {
            client.registerUser(user);
            model.addAttribute(MESSAGE, "User registered successfully");
        } catch (HttpStatusCodeException backendError) {
            String errorMessage = switch (backendError.getStatusCode()) {
                case HttpStatus.BAD_REQUEST -> "Incorrect data";
                case HttpStatus.CONFLICT -> "User with such email or username already exists";
                default -> "Something went wrong, try again later";
            };
            model.addAttribute(ERROR, errorMessage);
            retainEmailAndUsername(model, request.email(), request.username());
        }
    }

    private void retainEmailAndUsername(Model model, String email, String username) {
        model.addAttribute("email", email);
        model.addAttribute("username", username);
    }
}
