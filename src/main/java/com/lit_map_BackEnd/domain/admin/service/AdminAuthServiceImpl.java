package com.lit_map_BackEnd.domain.admin.service;


import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    @Autowired
    private MemberRepository memberRepository;

    public boolean isAdmin() {
        // 현재 로그인한 사용자의 인증 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // 인증되지 않았거나 로그인하지 않았을 경우
        }

        String username = authentication.getName(); // 로그인한 사용자의 이메일

        // 데이터베이스에서 사용자의 정보를 조회합니다.
        Optional<Member> optionalMember = memberRepository.findByLitmapEmail(username);
   return optionalMember
                .map(member -> {
                    System.out.println("Member Role Status: " + member.getRoleStatus());
                    return member.getRoleStatus() == MemberRoleStatus.ADMIN;
                })
                .orElse(false);
    }

}
