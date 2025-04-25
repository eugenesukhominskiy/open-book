package com.openbook.openbook.services;

import com.openbook.openbook.DTO.MemberDTO;
import com.openbook.openbook.enums.Role;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.MemberRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void create_ShouldSaveMemberWithEncodedPassword() {
        // given
        MemberDTO dto = new MemberDTO();
        dto.setUsername("john");
        dto.setEmail("john@example.com");
        dto.setPassword("pass123");
        dto.setRole(Role.READER);

        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(memberRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        Member saved = memberService.create(dto);

        // then
        assertEquals("john", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals("encoded_pass", saved.getPassword());
        assertEquals(Role.READER, saved.getRole());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void read_ShouldReturnMember_WhenExists() {
        // given
        Member member = new Member();
        member.setId(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        Member found = memberService.read(1L);

        // then
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void update_ShouldModifyUserFields_AndEncodePassword() {
        // given
        Long userId = 1L;
        Member existing = new Member();
        existing.setId(userId);
        existing.setUsername("old");
        existing.setEmail("old@email.com");
        existing.setPassword("oldpass");

        MemberDTO dto = new MemberDTO();
        dto.setUsername("newUser");
        dto.setEmail("new@email.com");
        dto.setPassword("newpass");

        when(memberRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_newpass");
        when(memberRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        Member updated = memberService.update(dto, userId);

        // then
        assertEquals("newUser", updated.getUsername());
        assertEquals("new@email.com", updated.getEmail());
        assertEquals("encoded_newpass", updated.getPassword());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // when
        memberService.delete(42L);

        // then
        verify(memberRepository).deleteById(42L);
    }

    @Test
    void findByUsername_ShouldReturnMember_WhenFound() {
        Member member = new Member();
        member.setUsername("test");

        when(memberRepository.findByUsername("test")).thenReturn(Optional.of(member));

        Optional<Member> result = memberService.findByUsername("test");

        assertTrue(result.isPresent());
        assertEquals("test", result.get().getUsername());
    }

    @Test
    void findAllMembers_ShouldReturnListOfMembers() {
        List<Member> list = List.of(new Member(), new Member());
        when(memberRepository.findAll()).thenReturn(list);

        List<Member> result = memberService.findAllMembers();

        assertEquals(2, result.size());
    }
}
