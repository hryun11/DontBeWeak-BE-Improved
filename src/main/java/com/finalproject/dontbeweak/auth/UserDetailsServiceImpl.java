package com.finalproject.dontbeweak.auth;

import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Autowired
    public UserDetailsServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //로그인 시 아이디를 찾을 수 없을 때
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("이 " + username + " 아이디는 존재하지 않습니다."));

        return new UserDetailsImpl(member);
    }
}
