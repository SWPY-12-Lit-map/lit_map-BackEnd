package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.entity.WorkCategoryGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkCategoryGenreRepository extends JpaRepository<WorkCategoryGenre, Long> {
    boolean existsByWorkAndCategoryAndGenre(Work work, Category category, Genre genre);
}
