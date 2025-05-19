package com.openbook.openbook.services;

import com.openbook.openbook.DTO.AuthResponse;
import com.openbook.openbook.DTO.LoginRequest;
import com.openbook.openbook.DTO.RegisterRequest;
import com.openbook.openbook.enums.Role;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(MemberRepository memberRepository, AuthenticationManager authManager, JwtTokenProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.authManager = authManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("You can not register with admin role");
        }

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        memberRepository.save(member);

        Authentication auth = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authenticatedPrincipal = authManager.authenticate(auth);

        return new AuthResponse(jwtProvider.generationToken(authenticatedPrincipal));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        return new AuthResponse(jwtProvider.generationToken(auth));
    }
}
