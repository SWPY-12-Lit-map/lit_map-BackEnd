package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query("select w from Work w where w.title like %:question%")
    List<Work> findWorksByTitle(String question);

    @Query("select w from Work w where w.content like %:question%")
    List<Work> findWorksByContents(String question);

    @Query("select w from Work w " +
            "where w.content like %:question% or w.title like %:question%")
    List<Work> findWorksByTitleAndContents(String question);

    @Query("select w " +
            "from Work w " +
            "join Publisher p on w.publisher.id = p.id " +
            "where p.publisherName like :question")
    List<Work> findWorksByPublisherName(String question);

    @Query("select w " +
            "from Work w " +
            "join Member m on w.member.id = m.id " +
            "where m.nickname like :question")
    List<Work> findWorksByMemberNickName(String question);
}
