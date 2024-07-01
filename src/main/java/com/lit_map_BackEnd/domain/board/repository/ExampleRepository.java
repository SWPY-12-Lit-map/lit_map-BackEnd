package com.lit_map_BackEnd.domain.board.repository;

import com.lit_map_BackEnd.domain.board.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepository extends JpaRepository<Example, Long> {

}
