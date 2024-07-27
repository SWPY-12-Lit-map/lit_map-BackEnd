package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.author.entity.Author;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.entity.WorkAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkAuthorRepository extends JpaRepository<WorkAuthor, Long> {
    List<WorkAuthor> findByWork(Work work);
    boolean existsByWorkAndAuthor(Work work, Author author);
    void deleteByWork(Work work);

    @Query("select wa.work " +
            "from WorkAuthor wa " +
            "join Author a on wa.author.id = a.id " +
            "join Version v on wa.work.id = v.work.id " +
            "left join RollBackVersion rv on v.versionName = rv.versionName " +
            "where a.name like :question " +
            "and (v.confirm = 'COMPLETE' or rv.confirm = 'COMPLETE')")
    List<Work> findByAuthorName(String question);

}
