package com.lit_map_BackEnd.domain.relation.repository;

import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<Work,Long> {


    //같은 카테고리, 같은 장르 조회 후 같은 작가에서 다른작가로 정렬
    @Query("SELECT w FROM Work w " +
            "WHERE w.id <> :workId " +  // Exclude the same work
            "AND w.category.id = (SELECT wcg.category.id FROM WorkCategoryGenre wcg WHERE wcg.work.id = :workId) " +
            "AND w.mainAuthor = (SELECT wa.author.name FROM WorkAuthor wa WHERE wa.work.id = :workId) " +
            "AND EXISTS (SELECT wg FROM WorkGenre wg WHERE wg.work.id = :workId AND wg.genre.id = w.id) " +
            "ORDER BY " +
            "CASE WHEN w.mainAuthor = (SELECT wa2.author.name FROM WorkAuthor wa2 WHERE wa2.work.id = :workId) THEN 0 ELSE 1 END, " +  // Sort by matching author_id first
            "w.title ASC")
    List<Work> findOtherWorksWithSameCategoryGenreSortedByAuthor(
            @Param("workId") Long workId
    );

    //같은 카테고리, 같은 작가 일때 다른 장르 조회
    @Query("SELECT w FROM Work w " +
            "WHERE w.id <> :workId " +
            "AND w.category.id = (SELECT wcg.category.id FROM WorkCategoryGenre wcg WHERE wcg.work.id = :workId) " +
            "AND w.mainAuthor = (SELECT wa.author.name FROM WorkAuthor wa WHERE wa.work.id = :workId) " +
            "AND NOT EXISTS (SELECT wg FROM WorkGenre wg WHERE wg.work.id = :workId AND wg.genre.id = w.id)")
    List<Work> findWorksWithSameCategoryAndAuthorButDifferentGenre(@Param("workId") Long workId);


}
