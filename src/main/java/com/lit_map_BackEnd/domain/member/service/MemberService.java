package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MailDto;

public interface MemberService {
    // 탈퇴 - 관리자 승인
    void requestWithdrawal(Long memberId);
    void approveWithdrawal(Long memberId);
}
