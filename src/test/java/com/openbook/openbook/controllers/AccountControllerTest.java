package com.openbook.openbook.controllers;

import com.openbook.openbook.DTO.MemberRequest;
import com.openbook.openbook.DTO.MemberResponse;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private Principal principal;

    @InjectMocks
    private AccountController accountController;

    private final String testUsername = "testuser";
    private Member testMember;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setUsername(testUsername);
        testMember.setEmail("test@example.com");

        memberRequest = new MemberRequest();
        memberRequest.setUsername("newusername");
        memberRequest.setEmail("new@example.com");
        memberRequest.setPassword("newpassword");
    }

    @Test
    void viewProfile_UserExists_ReturnsMemberResponse() {
        // Arrange
        when(principal.getName()).thenReturn(testUsername);
        when(memberService.findByUsername(testUsername)).thenReturn(Optional.of(testMember));

        // Act
        ResponseEntity<?> response = accountController.viewProfile(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MemberResponse);
        verify(memberService).findByUsername(testUsername);
    }

    @Test
    void viewProfile_UserNotFound_ReturnsNotFound() {
        // Arrange
        when(principal.getName()).thenReturn("nonexistent");
        when(memberService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = accountController.viewProfile(principal);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void updateAccount_UserExists_ReturnsUpdatedMember() {
        // Arrange
        when(principal.getName()).thenReturn(testUsername);
        when(memberService.findByUsername(testUsername)).thenReturn(Optional.of(testMember));
        when(memberService.update(memberRequest, testMember.getId())).thenReturn(testMember);

        // Act
        ResponseEntity<?> response = accountController.updateAccount(principal, memberRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MemberResponse);
        verify(memberService).update(memberRequest, testMember.getId());
    }

    @Test
    void updateAccount_UserNotFound_ReturnsNotFound() {
        // Arrange
        when(principal.getName()).thenReturn("nonexistent");
        when(memberService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = accountController.updateAccount(principal, memberRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(memberService, never()).update(any(), any());
    }

    @Test
    void deleteAccount_UserExists_ReturnsSuccessMessage() {
        // Arrange
        when(principal.getName()).thenReturn(testUsername);
        when(memberService.findByUsername(testUsername)).thenReturn(Optional.of(testMember));
        doNothing().when(memberService).delete(testMember.getId());

        // Act
        ResponseEntity<?> response = accountController.deleteAccount(principal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account deleted successfully!", response.getBody());
        verify(memberService).delete(testMember.getId());
    }

    @Test
    void deleteAccount_UserNotFound_ReturnsNotFound() {
        // Arrange
        when(principal.getName()).thenReturn("nonexistent");
        when(memberService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = accountController.deleteAccount(principal);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(memberService, never()).delete(any());
    }
}