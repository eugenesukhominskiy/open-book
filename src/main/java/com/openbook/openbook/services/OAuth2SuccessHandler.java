package com.openbook.openbook.services;

import com.openbook.openbook.enums.Role;
import com.openbook.openbook.model.Member;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.util.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        if (email == null) {
            Object login = oauthUser.getAttribute("login");
            if (login != null) {
                email = login + "@github.local";
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Cannot retrieve email from GitHub OAuth2 response\"}");
                return;
            }
        }

        String login = oauthUser.getAttribute("login");
        String username = login != null ? login : "github_user_" + UUID.randomUUID();
        String finalEmail = email;

        Member member = memberRepository.findByEmail(email).orElseGet(() -> {
            Member m = Member.builder()
                    .email(finalEmail)
                    .username(username)
                    .password("")
                    .role(Role.READER)
                    .build();
            return memberRepository.save(m);
        });

        String token = jwtProvider.generationToken(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        member.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
                )
        );

        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // лише якщо HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().flush();

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
