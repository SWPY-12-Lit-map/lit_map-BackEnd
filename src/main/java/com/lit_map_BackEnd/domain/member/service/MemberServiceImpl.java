package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.member.dto.MailDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
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
    public void requestWithdrawal(Long memberId) {
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            // 탈퇴 요청 플래그 설정
            member.setWithdrawalRequested(true);
            memberRepository.save(member);

            // 관리자에게 알림 이메일 전송
            String subject = "탈퇴 요청 알림";
            String text = "회원 " + member.getLitmapEmail() + "님이 탈퇴 요청을 했습니다.";
            // 관리자 이메일로 변경 필요
            emailService.sendEmail("sooho7767@naver.com", subject, text);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public void approveWithdrawal(Long memberId) {
        // 탈퇴 승인 로직
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            memberRepository.delete(member);

            // 탈퇴 승인 이메일 전송
            String email = member.getLitmapEmail();
            String subject = "탈퇴 승인 완료";
            String text = "회원님의 탈퇴 요청이 승인되었습니다.";
            emailService.sendEmail(email, subject, text);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    }

}
