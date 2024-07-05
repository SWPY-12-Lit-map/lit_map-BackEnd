package com.lit_map_BackEnd.domain.work.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Work;

import java.util.List;
import java.util.Map;

public interface VersionService {
    int insertRelationship(Map<String, Object> jsonMap) throws JsonProcessingException;

    List<VersionResponseDto> findVersionByWork(Work work);
}
