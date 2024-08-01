package com.lit_map_BackEnd.domain.admin.service;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;

import java.util.List;

public interface AdminMemberService {
    List<Member> getAllMembers(); // 모든 회원 정보를 조회하는 메소드
    List<Member> getMembersByStatus(MemberRoleStatus status); // 특정 상태의 회원들을 조회하는 메소드
    //Member approveMember(Long memberId); // 회원을 승인하는 메소드
}
