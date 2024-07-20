package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.member.dto.MailDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void requestMemberWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setRoleStatus(MemberRoleStatus.WITHDRAWN_MEMBER);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void approveMemberWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setRoleStatus(MemberRoleStatus.UNKNOWN_MEMBER);
        memberRepository.save(member);

        String subject = "탈퇴가 승인되었습니다.";
        String content = "회원님의 탈퇴 요청이 승인되었습니다. 그동안 이용해주셔서 감사합니다.";
        emailService.sendEmail(member.getLitmapEmail(), subject, content);
    }

}
