package com.lit_map_BackEnd.domain.work.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;

import java.util.List;
import java.util.Map;

public interface VersionService {
    int insertRelationship(Map<String, Object> jsonMap) throws JsonProcessingException;

    List<VersionResponseDto> findVersionByWork(Work work);

    // 기존에 존재하는 상황이면 여기서 수정한다.
    Version changeVersion(Double versionNum, String versionName, Map<String, Object> relationship, Work work);
}
