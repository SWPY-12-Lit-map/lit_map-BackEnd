package com.lit_map_BackEnd.domain.member.entity;

public enum Role {
    PUBLISHER_MEMBER, // 출판사 직원
    ADMIN,            // 관리자
    PENDING_MEMBER,   // 승인 대기 중인 회원
    APPROVED_MEMBER   // 승인 완료된 회원

    // 추가되어애 하는것
    // 출판사직원, 승인완료된 회원이 탈퇴한경우 '탈퇴한회원' 으로 추가
}
