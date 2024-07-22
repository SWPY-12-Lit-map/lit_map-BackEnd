package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MailDto;

public interface MemberService {
    // 1인작가 탈퇴 로직
    void requestMemberWithdrawal(Long memberId); // 회원 탈퇴 요청
    void approveMemberWithdrawal(Long memberId); // 승인 회원 탈퇴
}
