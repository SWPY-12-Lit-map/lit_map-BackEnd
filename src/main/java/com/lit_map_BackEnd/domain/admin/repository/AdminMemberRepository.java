package com.lit_map_BackEnd.domain.admin.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByMemberRoleStatus(MemberRoleStatus status); // 특정 상태의 회원들을 조회
}
