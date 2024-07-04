package com.lit_map_BackEnd.domain.genre.repository;

import com.lit_map_BackEnd.domain.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Genre findByName(String name);
}
