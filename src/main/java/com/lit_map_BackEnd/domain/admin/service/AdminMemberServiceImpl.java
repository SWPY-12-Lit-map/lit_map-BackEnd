package com.lit_map_BackEnd.domain.admin.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.admin.repository.AdminMemberRepository;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;
    private final AdminMemberRepository adminMemberRepository;
    private final MailService mailService;
    private final AdminAuthService adminAuthService;


    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll(); // 데이터베이스에서 모든 회원 정보 조회
    }

    @Override
    public List<Member> getMembersByStatus(MemberRoleStatus status) {
        return adminMemberRepository.findByMemberRoleStatus(status); // 특정 상태의 회원들을 조회
    }

    //회원 탈퇴 승인
    @Transactional
    public void approveWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

//        if (!adminAuthService.isAdmin()) {
//            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
//        } // 관리자권한
        member.setMemberRoleStatus(MemberRoleStatus.UNKNOWN_MEMBER);
        memberRepository.save(member);

    }

    //회원승인
    @Transactional
    public Member approveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

//        if (!adminAuthService.isAdmin()) {
//            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
//        } // 관리자 권한

        member.setMemberRoleStatus(MemberRoleStatus.ACTIVE_MEMBER);
        memberRepository.save(member);

        // 승인 이메일 전송
        String subject = "[litmap] 회원 가입 승인";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,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\"/>"
                + "<br><h2>회원 가입 승인</h2><h4>"
                + member.getName()
                + "님 회원 가입이 승인되었습니다. 환영합니다!</h4></div>";

        mailService.sendEmail(member.getLitmapEmail(), subject, content);
        return member;
    }

    //회원 강제 탈퇴
    @Transactional
    public void forceWithdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

//        if (!adminAuthService.isAdmin()) {
//            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
//        } // 관리자 권한

        member.setMemberRoleStatus(MemberRoleStatus.UNKNOWN_MEMBER);
        memberRepository.save(member);

        // 탈퇴 이메일 전송
        String subject = "[litmap] 회원 탈퇴 처리";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,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\"/>"
                + "<br><h2>회원 탈퇴 처리</h2><h4>"
                + member.getName()
                + "님 회원 탈퇴가 처리되었습니다.</h4></div>";

        mailService.sendEmail(member.getLitmapEmail(), subject, content);
    }

}
