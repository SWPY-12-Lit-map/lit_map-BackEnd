package com.lit_map_BackEnd.domain.member.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getAuthorities();
    } // 사용자 권한 반환, Security에서 사용자의 권한을 확인할 때 사용

    @Override
    public String getPassword() {
        return member.getPassword();
    } // 사용자의 비밀번호를 반환

    @Override
    public String getUsername() {
        return member.getLitmapEmail();
    } // 사용자의 이름(여기서는 이메일)을 반환

    public boolean isAdmin() {
        return member.getMemberRoleStatus() == MemberRoleStatus.ADMIN;
    } // 사용자가 관리자인지 확인하는 메서드

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
