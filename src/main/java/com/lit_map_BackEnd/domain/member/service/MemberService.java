package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MailDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;

import java.util.List;

public interface MemberService {
    void requestWithdrawal(Long memberId); // 회원 탈퇴 요청
    void approveWithdrawal(Long memberId); // 회원 탈퇴 승인
}
