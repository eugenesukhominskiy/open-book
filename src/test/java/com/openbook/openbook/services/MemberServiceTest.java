package com.openbook.openbook.services;

import com.openbook.openbook.DTO.MemberRequest;
import com.openbook.openbook.enums.Role;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private MemberRequest memberRequest;
    private Member existingMember;
    private final Long memberId = 1L;
    private final String encodedPassword = "$2a$10$encodedPassword";

    @BeforeEach
    void setUp() {
        memberRequest = new MemberRequest();
        memberRequest.setUsername("testuser");
        memberRequest.setEmail("test@example.com");
        memberRequest.setPassword("password123");
        memberRequest.setRole(Role.READER);

        existingMember = new Member();
        existingMember.setId(memberId);
        existingMember.setUsername("testuser");
        existingMember.setEmail("test@example.com");
        existingMember.setPassword(encodedPassword);
    }

    @Test
    void create_WithValidRequest_ReturnsMember() {
        // Arrange
        when(passwordEncoder.encode(memberRequest.getPassword())).thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        // Act
        Member result = memberService.create(memberRequest);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder).encode(memberRequest.getPassword());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void read_ExistingId_ReturnsMember() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

        // Act
        Member result = memberService.read(memberId);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void read_NonExistingId_ReturnsNull() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act
        Member result = memberService.read(memberId);

        // Assert
        assertNull(result);
    }

    @Test
    void update_ExistingUser_UpdatesFields() {
        // Arrange
        MemberRequest updateRequest = new MemberRequest();
        updateRequest.setUsername("newusername");
        updateRequest.setEmail("new@example.com");
        updateRequest.setPassword("newpassword");
        updateRequest.setRole(Role.READER);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("$2a$10$newEncodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        // Act
        Member result = memberService.update(updateRequest, memberId);

        // Assert
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newusername", result.getUsername());
        verify(passwordEncoder).encode(updateRequest.getPassword());
    }

    @Test
    void update_NonExistingUser_ThrowsException() {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                memberService.update(memberRequest, memberId)
        );
        verify(memberRepository, never()).save(any());
    }

    @Test
    void delete_ValidId_DeletesMember() {
        // Arrange
        doNothing().when(memberRepository).deleteById(memberId);

        // Act
        memberService.delete(memberId);

        // Assert
        verify(memberRepository).deleteById(memberId);
    }

    @Test
    void findByUsername_ExistingUser_ReturnsMember() {
        // Arrange
        when(memberRepository.findByUsername("testuser")).thenReturn(Optional.of(existingMember));

        // Act
        Optional<Member> result = memberService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_NonExistingUser_ReturnsEmpty() {
        // Arrange
        when(memberRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<Member> result = memberService.findByUsername("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllMembers_ReturnsAllMembers() {
        // Arrange
        when(memberRepository.findAll()).thenReturn(List.of(existingMember));

        // Act
        List<Member> result = memberService.findAllMembers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}