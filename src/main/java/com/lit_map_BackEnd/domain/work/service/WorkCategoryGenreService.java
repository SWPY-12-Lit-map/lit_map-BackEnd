package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.domain.work.entity.WorkCategoryGenre;

import java.util.List;

public interface WorkCategoryGenreService {
    List<WorkCategoryGenre> findWorks(Long categoryId, Long genreId);
}
