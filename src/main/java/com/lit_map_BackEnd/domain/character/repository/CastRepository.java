package com.lit_map_BackEnd.domain.character.repository;

import com.lit_map_BackEnd.domain.character.entity.Cast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
}
