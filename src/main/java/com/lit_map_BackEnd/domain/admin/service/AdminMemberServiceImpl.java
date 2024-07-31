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

    @Override
    @Transactional
    public Member approveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setMemberRoleStatus(MemberRoleStatus.ACTIVE_MEMBER);
        Member approvedMember = memberRepository.save(member);

        // 승인 이메일 전송
        String subject = "[litmap] 회원 가입 승인";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0qMPdgAAAAASUVORK5CYII=\\\"/>"
                + "<br><h2>회원 가입 승인</h2><h4>"
                + member.getName()
                + "님 회원 가입이 승인되었습니다. 환영합니다!</h4></div>";

        emailService.sendEmail(member.getLitmapEmail(), subject, content);

        return approvedMember;
    }
}
