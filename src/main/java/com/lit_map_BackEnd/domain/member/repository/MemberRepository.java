package com.lit_map_BackEnd.domain.member.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLitmapEmail(String litmapEmail);
    Optional<Member> findByWorkEmail(String workEmail);
    Optional<Member> findByNickname(String nickname);

    boolean existsByLitmapEmail(String litmapEmail);

    @Modifying // 찾아서 수정하기
    @Query("UPDATE Member m SET m.password = :password WHERE m.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
