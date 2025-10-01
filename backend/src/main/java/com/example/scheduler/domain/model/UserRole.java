package com.example.scheduler.domain.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + value;
    }
}
