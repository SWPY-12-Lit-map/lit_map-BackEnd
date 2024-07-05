package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;

public interface WorkService {
    int saveWork(WorkRequestDto workRequestDto);

    WorkResponseDto getWork(Long workId);
}
