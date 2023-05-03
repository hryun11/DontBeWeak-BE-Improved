package com.finalproject.dontbeweak.auth;

import com.finalproject.dontbeweak.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Member member;

    public UserDetailsImpl(Member member){ this.member = member; }

    public Member getUser(){return member;}

    //비밀번호
    @Override
    public String getPassword() {
        return  member.getPassword();
    }

    //아이디
    @Override
    public String getUsername() {
        return member.getUsername();
    }


    //계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //사용자 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }


    //해당 유저의 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole());
        list.add(grantedAuthority);
        return list;
    }
}
