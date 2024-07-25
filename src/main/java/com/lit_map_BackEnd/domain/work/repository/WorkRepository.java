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

    @Query("select w from Work w where w.member = :member")
    List<Work> findByMember(Member member);

    @Modifying
    @Query("update Work w set w.view = w.view + 1 where w.id = :workId")
    void countUpView(Long workId);

    @Query("select w from Work w join fetch w.category join fetch w.member order by w.view desc")
    Slice<Work> findWorks(Pageable pageable);

    @Query("SELECT w FROM Work w " +
            "JOIN Version wv ON w.id = wv.work.id " +
            "LEFT JOIN RollBackVersion rv ON wv.versionName = rv.versionName " +
            "WHERE w.title LIKE %:question% " +
            "AND (wv.confirm = 'COMPLETE' OR rv.confirm = 'COMPLETE')")
    List<Work> findWorksByTitle(String question);

    @Query("SELECT w FROM Work w " +
            "JOIN Version wv ON w.id = wv.work.id " +
            "LEFT JOIN RollBackVersion rv ON wv.versionName = rv.versionName " +
            "WHERE w.content LIKE %:question% " +
            "AND (wv.confirm = 'COMPLETE' OR rv.confirm = 'COMPLETE')")
    List<Work> findWorksByContents(String question);

    @Query("SELECT w FROM Work w " +
            "JOIN Version v on w.id = v.work.id " +
            "LEFT JOIN RollBackVersion rv ON v.versionName = rv.versionName " +
            "WHERE (w.content LIKE %:question% or w.title like %:question%) " +
            "AND (v.confirm = 'COMPLETE' OR rv.confirm = 'COMPLETE')")
    List<Work> findWorksByTitleAndContents(String question);

    @Query("select w " +
            "from Work w " +
            "join Publisher p on w.publisher.id = p.id " +
            "join Version v on w.id = v.work.id " +
            "left join RollBackVersion rv on v.versionName = rv.versionName " +
            "where p.publisherName like %:question% " +
            "and (v.confirm = 'COMPLETE' or rv.confirm = 'COMPLETE')")
    List<Work> findWorksByPublisherName(String question);

    @Query("select w " +
            "from Work w " +
            "join Member m on w.member.id = m.id " +
            "join Version v on w.id = v.work.id " +
            "left join RollBackVersion rv on v.versionName = rv.versionName " +
            "where m.nickname like :question " +
            "and (v.confirm = 'COMPLETE' or rv.confirm = 'COMPLETE')")
    List<Work> findWorksByMemberNickName(String question);
}
