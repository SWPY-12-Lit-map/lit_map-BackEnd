package com.lit_map_BackEnd.domain.relation.service;

import com.lit_map_BackEnd.domain.relation.dto.RelationDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Work;

import java.util.List;

public interface RelationService{

    List<RelationDto> findRelatedWorks(Long workId);
}
