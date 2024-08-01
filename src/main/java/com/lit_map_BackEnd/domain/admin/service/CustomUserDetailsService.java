package com.lit_map_BackEnd.domain.admin.service;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional<Member> 타입으로 조회
        Optional<Member> optionalMember = memberRepository.findByLitmapEmail(username);

        // 값이 없으면 예외 발생
        Member member = optionalMember.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convert MemberRoleStatus to GrantedAuthority
        List<SimpleGrantedAuthority> authorities = member.getRoleStatus() != null ?
                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRoleStatus().name())) :
                Collections.emptyList();

        return new User(
                member.getLitmapEmail(),
                member.getPassword(),
                authorities
        );
    }
}
