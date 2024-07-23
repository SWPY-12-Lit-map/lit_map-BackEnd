package com.lit_map_BackEnd.domain.member.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Member findByEmail(String email);
    //Member findById(id);
    Member findByMemberId(Long memberId);
}
