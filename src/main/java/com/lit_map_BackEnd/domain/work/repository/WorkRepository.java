package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    boolean existsByTitle(String title);
    Work findByTitle(String title);
    void deleteById(Long workId);

    List<Work> findByMember(Member member);
}
