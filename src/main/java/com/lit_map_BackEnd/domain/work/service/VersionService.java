package com.lit_map_BackEnd.domain.work.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface VersionService {
    int insertRelationship(Map<String, Object> jsonMap) throws JsonProcessingException;
}
