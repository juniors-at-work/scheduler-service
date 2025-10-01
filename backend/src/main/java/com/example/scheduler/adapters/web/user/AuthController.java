package com.example.scheduler.adapters.web.user;

import com.example.scheduler.adapters.dto.AuthRequest;
import com.example.scheduler.adapters.dto.AuthResponse;
import com.example.scheduler.adapters.dto.RefreshTokenRequest;
import com.example.scheduler.adapters.dto.user.RegisterRequest;
import com.example.scheduler.application.service.AuthService;
import com.example.scheduler.application.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        log.info("Received request to register user: username = {}", request.username());
        log.debug("Register request = {}", request);
        AuthResponse response = userService.registerUser(request);
        log.info("Responded with tokens issued for username = {}", request.username());
        return response;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.loginUser(request.username(), request.password());
    }

    @PostMapping("/refresh")
    public AuthResponse refreshTokens(@RequestBody RefreshTokenRequest request) {
        return authService.refreshTokens(request.token());
    }
}