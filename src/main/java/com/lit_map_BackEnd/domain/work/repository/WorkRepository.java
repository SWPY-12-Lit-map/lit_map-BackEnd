package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.author.entity.Author;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    boolean existsByTitle(String title);
    Work findByTitle(String title);

    void deleteById(Long workId);

    Work findByMemberId(Long memberId);

/*
    // 동일한 작가일 때, 같은 카테고리를 가진 작품 추천
    @Query("SELECT w FROM Work w WHERE w.author = :author AND w IN (SELECT w2 FROM Work w2 JOIN w2.categories c WHERE c IN :categories) ORDER BY w.name")
    List<Work> findByAuthorAndCategoriesInOrderByNamе(Author author, List<Category> categories);

    // 다른 작가일 때, 같은 카테고리를 가진 작품 추천
    @Query("SELECT w FROM Work w WHERE w.author <> :author AND w IN (SELECT w2 FROM Work w2 JOIN w2.categories c WHERE c IN :categories) ORDER BY w.name")
    List<Work> findByCategoriesInOrderByNamе(List<Category> categories);
*/
    // 동일한 작가일 때, 같은 카테고리를 가진 작품 추천
    List<Work> findByAuthorAndCategoriesInOrderByNamе(Author author, List<Category> categories);

    // 다른 작가일 때, 같은 카테고리를 가진 작품 추천
    List<Work> findByAuthorNotAndCategoriesInOrderByNamе(Author author, List<Category> categories);


}
