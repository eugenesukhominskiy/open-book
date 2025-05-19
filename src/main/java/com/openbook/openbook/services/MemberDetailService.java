package com.openbook.openbook.services;

import com.openbook.openbook.repository.MemberRepository;
import com.openbook.openbook.security.MemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Autowired
    public MemberDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username: " + username);
        return memberRepository.findByUsername(username) // Ищем по username
                .map(MemberDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
