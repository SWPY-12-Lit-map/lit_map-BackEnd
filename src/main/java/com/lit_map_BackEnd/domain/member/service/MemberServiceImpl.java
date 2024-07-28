package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final AdminService adminService;

    @Override
    @Transactional
    public void requestWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setMemberRoleStatus(MemberRoleStatus.WITHDRAWN_MEMBER);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void approveWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        if (!adminService.isAdmin()) {
            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
        }
            member.setMemberRoleStatus(MemberRoleStatus.UNKNOWN_MEMBER);
            memberRepository.save(member);

    }

    //관리자 - 회원 강제 탈퇴
}