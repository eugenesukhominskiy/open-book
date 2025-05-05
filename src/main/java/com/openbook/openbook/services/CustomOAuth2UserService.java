package com.openbook.openbook.services;

import com.openbook.openbook.enums.Role;
import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.security.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Autowired
    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oauth2User.getAttributes();
        String githubId = String.valueOf(attributes.get("id"));
        String username = (String) attributes.get("login");
        String email = (String) attributes.get("email");

        if (email == null || email.isBlank()) {
            email = githubId + "@users.noReply.github.com";
        }

        Optional<Member> memberOptional = memberRepository.findByGithubId(githubId);

        Member member;
        if (memberOptional.isPresent()) {
            member = memberOptional.get();
            member.setUsername(username);
            member.setEmail(email);
            memberRepository.save(member);
        } else {
            member = new Member();
            member.setGithubId(githubId);
            member.setUsername(username);
            member.setEmail(email);
            member.setRole(Role.READER);
            member.setPassword(null);
            member = memberRepository.save(member);
        }

        return new MemberDetails(member, attributes);
    }
}
