package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.entity.WorkAuthor;
import com.lit_map_BackEnd.domain.work.entity.WorkGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkAuthorRepository extends JpaRepository<WorkAuthor, Long> {
    List<WorkAuthor> findByWork(Work work);
}
