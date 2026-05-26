package com.example.cyan.user.service;

import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.model.enums.UserRole;
import com.example.cyan.user.dto.UserResponse;
import com.example.cyan.user.model.User;
import com.example.cyan.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHashService passwordHashService;

    public UserService(UserRepository userRepository, PasswordHashService passwordHashService) {
        this.userRepository = userRepository;
        this.passwordHashService = passwordHashService;
    }

    public UserResponse authenticate(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isActive()) {
            throw new BadRequestException("User account is inactive");
        }
        if (!passwordHashService.matches(password, user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        return toResponse(user);
    }

    public UserResponse register(String email, String rawPassword, String fullName) {
        String normalizedEmail = normalizeEmail(email);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BadRequestException("Email is already registered");
        }

        return toResponse(createUser(normalizedEmail, rawPassword, fullName, UserRole.CUSTOMER));
    }

    public User ensureAdminUser(String email, String rawPassword, String fullName) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseGet(() -> createUser(email, rawPassword, fullName, UserRole.ADMIN));
    }

    public User createUser(String email, String rawPassword, String fullName, UserRole role) {
        User user = new User();
        user.setEmail(normalizeEmail(email));
        user.setPasswordHash(passwordHashService.hash(rawPassword));
        user.setFullName(fullName == null || fullName.isBlank() ? "System User" : fullName.trim());
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
