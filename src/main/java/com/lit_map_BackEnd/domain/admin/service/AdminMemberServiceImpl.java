package com.lit_map_BackEnd.domain.admin.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.admin.repository.AdminMemberRepository;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.member.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;
    private final AdminMemberRepository adminMemberRepository;
    private final EmailService emailService;

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll(); // 데이터베이스에서 모든 회원 정보 조회
    }

    @Override
    public List<Member> getMembersByStatus(MemberRoleStatus status) {
        return adminMemberRepository.findByMemberRoleStatus(status); // 특정 상태의 회원들을 조회
    }

}
