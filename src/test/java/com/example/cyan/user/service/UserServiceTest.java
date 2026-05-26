package com.example.cyan.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.cyan.common.exception.BadRequestException;
import com.example.cyan.common.model.enums.UserRole;
import com.example.cyan.user.model.User;
import com.example.cyan.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHashService passwordHashService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordHashService);
    }

    @Test
    void registerCreatesActiveCustomerUser() {
        when(userRepository.existsByEmailIgnoreCase("ngoc@example.com")).thenReturn(false);
        when(passwordHashService.hash("secret123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId("user-1");
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
            return user;
        });

        var response = userService.register(" Ngoc@Example.com ", "secret123", "Ngoc The");

        assertEquals("user-1", response.id());
        assertEquals("ngoc@example.com", response.email());
        assertEquals("Ngoc The", response.fullName());
        assertEquals(UserRole.CUSTOMER, response.role());
        assertEquals(true, response.active());
        verify(userRepository).existsByEmailIgnoreCase("ngoc@example.com");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("ngoc@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> userService.register("ngoc@example.com", "secret123", "Ngoc The"));
    }
}
