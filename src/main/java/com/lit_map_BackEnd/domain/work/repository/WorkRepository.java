package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.entity.Work;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    boolean existsByTitle(String title);
    Work findByTitle(String title);
    void deleteById(Long workId);
    List<Work> findByMember(Member member);

    @Modifying
    @Query("update Work w set w.view = w.view + 1 where w.id = :workId")
    void countUpView(Long workId);

    @Query("select w from Work w join fetch w.category join fetch w.member order by w.view desc")
    Slice<Work> findWorks(Pageable pageable);
}
