package com.openbook.openbook.services;

import com.openbook.openbook.DTO.MemberDTO;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(MemberDTO memberDTO) {
        if (memberRepository.findByUsername(memberDTO.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        Member user = new Member();
        user.setRole(memberDTO.getRole());
        user.setEmail(memberDTO.getEmail());
        user.setUsername(memberDTO.getUsername());
        user.setPassword(passwordEncoder.encode(memberDTO.getPassword()));

        memberRepository.save(user);
    }
}
