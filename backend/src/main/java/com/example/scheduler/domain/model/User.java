package com.example.scheduler.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String email,
        String passwordHash,
        String role,
        Instant createdAt,
        Instant updatedAt
) {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;
        private String passwordHash;
        private String role;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder() {
        }

        public Builder(User user) {
            this.id = user.id();
            this.username = user.username();
            this.email = user.email();
            this.passwordHash = user.passwordHash();
            this.role = user.role();
            this.createdAt = user.createdAt();
            this.updatedAt = user.updatedAt();
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            Objects.requireNonNull(id, "id cannot be null");
            Objects.requireNonNull(username, "username cannot be null");
            Objects.requireNonNull(email, "email cannot be null");
            Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
            Objects.requireNonNull(role, "role cannot be null");
            Objects.requireNonNull(createdAt, "createdAt cannot be null");
            Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
            return new User(id, username, email, passwordHash, role, createdAt, updatedAt);
        }
    }
}
