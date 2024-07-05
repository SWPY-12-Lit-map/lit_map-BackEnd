package com.lit_map_BackEnd.domain.work.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService{

    private final VersionRepository versionRepository;
    private final WorkRepository workRepository;

    @Override
    @Transactional
    public int insertRelationship(Map<String, Object> versionRequestDto) {
        long findWorkId = ((Integer) versionRequestDto.get("workId")).longValue();
        Double version = (Double) versionRequestDto.get("version");

        Work work = workRepository.findById(findWorkId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));
        Version findVersion = versionRepository.findByVersionNumAndWork(version, work);


        try {
            findVersion.changeRelationship(versionRequestDto);
        } catch (JsonProcessingException e) {
            throw new BusinessExceptionHandler(ErrorCode.JSON_PARSING_ERROR);
        }

        return 1;
    }
}
