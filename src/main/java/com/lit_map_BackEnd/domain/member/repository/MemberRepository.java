package com.lit_map_BackEnd.domain.member.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLitmapEmail(String litmapEmail); // 릿맵 이메일로 회원 조회
    Optional<Member> findByWorkEmail(String workEmail); // 업무용 이메일로 회원 조회
    Optional<Member> findByNickname(String nickname); // 닉네임으로 회원 조회
    Optional<Member> findByName(String name);

    boolean existsByLitmapEmail(String litmapEmail); // 릿맵 이메일 존재 여부 확인

    @Modifying // 찾아서 수정하기
    @Query("UPDATE Member m SET m.password = :password WHERE m.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);

}