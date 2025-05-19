package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.AuthResponse;
import com.openbook.openbook.DTO.LoginRequest;
import com.openbook.openbook.DTO.RegisterRequest;
import com.openbook.openbook.enums.Role;
import com.openbook.openbook.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.READER);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        authResponse = new AuthResponse("jwtToken");
    }

    @Test
    void register_WithAdminRole_ReturnsBadRequest() {
        // Arrange
        registerRequest.setRole(Role.ADMIN);
        doThrow(new IllegalArgumentException("You can not register with admin role"))
                .when(authService).register(registerRequest);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed: You can not register with admin role", response.getBody());
    }

    @Test
    void register_WithExistingEmail_ReturnsBadRequest() {
        // Arrange
        doThrow(new IllegalArgumentException("User with this email already exists"))
                .when(authService).register(registerRequest);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed: User with this email already exists", response.getBody());
    }

    @Test
    void login_WithValidCredentials_ReturnsAuthResponse() {
        // Arrange
        when(authService.login(loginRequest)).thenReturn(authResponse);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(authService).login(loginRequest);
    }

    @Test
    void login_WithInvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        when(authService.login(loginRequest))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }
}