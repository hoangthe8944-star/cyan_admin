package com.example.cyan.user.dto;

import java.time.Instant;

import com.example.cyan.common.model.enums.UserRole;

public record UserResponse(
        String id,
        String email,
        String fullName,
        UserRole role,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}
