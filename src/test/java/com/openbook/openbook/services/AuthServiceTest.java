package com.openbook.openbook.services;

import com.openbook.openbook.DTO.AuthResponse;
import com.openbook.openbook.DTO.LoginRequest;
import com.openbook.openbook.DTO.RegisterRequest;
import com.openbook.openbook.enums.Role;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.services.AuthService;
import com.openbook.openbook.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private Member existingMember;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest(
                Role.READER,
                "test@example.com",
                "testuser",
                "password123"
        );

        validLoginRequest = new LoginRequest(
                "testuser",
                "password123"
        );

        existingMember = Member.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.READER)
                .build();
    }

    @Test
    void register_WithValidData_ReturnsAuthResponse() {
        // Arrange
        when(memberRepository.findByEmail(validRegisterRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword()))
                .thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class)))
                .thenReturn(existingMember);

        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any()))
                .thenReturn(auth);
        when(jwtProvider.generationToken(auth))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.register(validRegisterRequest);

        // Assert
        assertNotNull(response);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void register_WithAdminRole_ThrowsException() {
        // Arrange
        RegisterRequest adminRequest = new RegisterRequest(
                Role.ADMIN,
                "admin@example.com",
                "admin",
                "admin123"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(adminRequest)
        );
        assertEquals("You can not register with admin role", exception.getMessage());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void register_WithExistingEmail_ThrowsException() {
        // Arrange
        when(memberRepository.findByEmail(validRegisterRequest.getEmail()))
                .thenReturn(Optional.of(existingMember));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(validRegisterRequest)
        );
        assertEquals("User with this email already exists", exception.getMessage());
        verify(memberRepository, never()).save(any());
    }

    @Test
    void login_WithValidCredentials_ReturnsAuthResponse() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any()))
                .thenReturn(auth);
        when(jwtProvider.generationToken(auth))
                .thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        verify(authManager).authenticate(any());
    }

    @Test
    void login_WithInvalidCredentials_ThrowsException() {
        // Arrange
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(validLoginRequest)
        );
        verify(jwtProvider, never()).generationToken(any());
    }

    @Test
    void register_PasswordIsEncoded() {
        // Arrange
        when(memberRepository.findByEmail(validRegisterRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword()))
                .thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class)))
                .thenReturn(existingMember);
        when(authManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));
        when(jwtProvider.generationToken(any()))
                .thenReturn("jwtToken");

        // Act
        authService.register(validRegisterRequest);

        // Assert
        verify(passwordEncoder).encode(validRegisterRequest.getPassword());
        verify(memberRepository).save(argThat(member ->
                member.getPassword().equals("encodedPassword")
        ));
    }
}