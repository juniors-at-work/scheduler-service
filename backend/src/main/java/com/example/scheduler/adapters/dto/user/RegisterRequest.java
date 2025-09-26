package com.example.scheduler.adapters.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotNull @Size(max = 255) String username,
    @NotNull String password,
    @NotEmpty @Email @Size(max = 255) String email
) {}
