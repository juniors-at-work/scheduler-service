package com.example.scheduler.adapters.dto.user;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email
) {
}
