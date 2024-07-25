package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MailDto;

public interface MemberService {
    void requestWithdrawal(Long memberId); // 회원 탈퇴 요청
    void approveWithdrawal(Long memberId); // 회원 탈퇴 승인
}
