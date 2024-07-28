package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
/*
    @Autowired
    private MemberRepository memberRepository;

    public boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = auth.getName();
        System.out.println("현재 계정이름 : "+ auth.getName());

        Member member = memberRepository.findByName(currentUserName)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR));

        return member.getRoleStatus() == MemberRoleStatus.ADMIN;
    }
  */
        public boolean isAdmin() {

            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 인증 정보가 없거나 Principal이 null인 경우 false 반환
            if (authentication == null || authentication.getPrincipal() == null) {
                return false;
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                //return userDetails.isAdmin();
                return true;
            }
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }


}
