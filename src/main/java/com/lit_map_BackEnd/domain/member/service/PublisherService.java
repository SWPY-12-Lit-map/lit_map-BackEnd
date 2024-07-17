package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MemberDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.entity.Publisher;

public interface PublisherService {
    // 출판사 탈퇴 - 관리자 승인 -> 탈퇴는 회원 한명씩만 가능
    void requestWithdrawalAll(Long publisherId);
    void requestWithdrawalSpecific(Long publisherId, Long memberId);
    void approveWithdrawalAll(Long publisherId);
    void approveWithdrawalSpecific(Long publisherId, Long memberId);
}
