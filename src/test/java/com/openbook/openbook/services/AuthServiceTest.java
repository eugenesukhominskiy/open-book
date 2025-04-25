package com.openbook.openbook.services;

import com.openbook.openbook.DTO.MemberDTO;
import com.openbook.openbook.enums.Role;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authService = new AuthService(memberRepository, passwordEncoder);
    }

    @Test
    void register_ShouldSaveNewMember_WhenUserDoesNotExist() {
        // given
        MemberDTO dto = new MemberDTO();
        dto.setUsername("test_user");
        dto.setEmail("test@user.com");
        dto.setPassword("test_password");
        dto.setRole(Role.READER);

        when(memberRepository.findByUsername("test_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("test_password")).thenReturn("encodeSecret");

        // when
        authService.register(dto);

        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());

        Member saved = captor.getValue();
        assertEquals("test_user", saved.getUsername());
        assertEquals("test@user.com", saved.getEmail());
        assertEquals("encodeSecret", saved.getPassword());
        assertEquals(Role.READER, saved.getRole());
    }

    @Test
    void register_ShouldThrowException_whenUserAlreadyExists() {
        // given
        MemberDTO dto = new MemberDTO();
        dto.setUsername("existing_user");

        when(memberRepository.findByUsername("existing_user")).thenReturn(Optional.of(new Member()));

        // when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(dto));
        assertEquals("User already exists", exception.getMessage());

        verify(memberRepository, never()).save(any());
    }
}