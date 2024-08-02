package com.lit_map_BackEnd.domain.admin.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.admin.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    private CustomUserDetails customUserDetails;

    @Override
    public UserDetails loadUserByUsername(String litmapEmail) throws UsernameNotFoundException {
        // Optional<Member> 타입으로 조회
        System.out.println("username (litmapEmail): " + litmapEmail);
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + litmapEmail));

        // Convert MemberRoleStatus to GrantedAuthority
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + member.getMemberRoleStatus().name()));

        // CustomUserDetails로 반환
        return new CustomUserDetails(member);
    }
    /*
    @Override
    public UserDetails loadUserByUsername(String litmapEmail) throws UsernameNotFoundException {
        // Optional<Member> 타입으로 조회
        System.out.println("username( litmapEmail ) : " + litmapEmail);
        Optional<Member> optionalMember = memberRepository.findByLitmapEmail(litmapEmail);

        // 값이 없으면 예외 발생
        Member member = optionalMember.orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.NONE));

        // Convert MemberRoleStatus to GrantedAuthority
        List<SimpleGrantedAuthority> authorities = member.getRoleStatus() != null ?
                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRoleStatus().name())) :
                Collections.emptyList();

        return new User(
                member.getLitmapEmail(),
                member.getPassword(),
                authorities
        );*/
    }

