package com.openbook.openbook.services;

import com.openbook.openbook.DTO.MemberDTO;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.security.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(MemberDTO memberDTO) {
        Member member = Member.builder()
                .role(memberDTO.getRole())
                .email(memberDTO.getEmail())
                .username(memberDTO.getUsername())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .build();
        return memberRepository.save(member);
    }

    public Member read(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member update(MemberDTO dto, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        member.setEmail(dto.getEmail());
        member.setUsername(dto.getUsername());
        member.setPassword(passwordEncoder.encode(dto.getPassword())); // если шифруешь
        return memberRepository.save(member);
    }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }
}