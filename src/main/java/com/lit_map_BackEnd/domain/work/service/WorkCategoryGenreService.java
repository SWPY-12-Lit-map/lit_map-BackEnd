package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.repository.GenreRepository;
import com.lit_map_BackEnd.domain.work.entity.WorkCategoryGenre;
import com.lit_map_BackEnd.domain.work.repository.WorkCategoryGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkCategoryGenreService {

    private final WorkCategoryGenreRepository workCategoryGenreRepository;
    private final CategoryRepository categoryRepository;
    private GenreRepository genreRepository;

    public List<WorkCategoryGenre> findWorks(Long cId, Long gId) {
        Category category = categoryRepository.findById(cId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND));

        Genre genre = genreRepository.findById(gId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.GENRE_NOT_FOUND));

        return workCategoryGenreRepository.findWorkCategoryGenreByCategoryAndGenre(category, genre);
    }
}
