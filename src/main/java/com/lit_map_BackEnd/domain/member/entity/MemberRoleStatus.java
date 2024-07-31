package com.lit_map_BackEnd.domain.member.entity;

public enum MemberRoleStatus {
    ACTIVE_MEMBER,          // 활성화된 회원, 출판사 직원
    PENDING_MEMBER,         // 승인 대기 중인 회원
    UNKNOWN_MEMBER,         // 탈퇴한 회원
    WITHDRAWN_MEMBER,       // 탈퇴요청한 출판사직원, 1인회원
    PUBLISHER_MEMBER,       // 출판사직원
    ADMIN                   // 관리자
}
