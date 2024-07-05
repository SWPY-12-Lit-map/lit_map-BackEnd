package com.lit_map_BackEnd.domain.character.repository;

import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CastRepository extends JpaRepository<Cast, Long> {
    List<Cast> findByWork(Work work);
}
