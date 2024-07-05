package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.entity.WorkGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkGenreRepository extends JpaRepository<WorkGenre, Long> {
    List<WorkGenre> findByWork(Work work);
}
