package com.lit_map_BackEnd.domain.author.repository;

import com.lit_map_BackEnd.domain.author.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findByName(String name);
}
