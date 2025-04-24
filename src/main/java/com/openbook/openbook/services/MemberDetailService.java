package com.openbook.openbook.services;

import com.openbook.openbook.models.Member;
import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.security.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByUsername(username);

        if (member.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new MemberDetails(member.get());
    }
}
