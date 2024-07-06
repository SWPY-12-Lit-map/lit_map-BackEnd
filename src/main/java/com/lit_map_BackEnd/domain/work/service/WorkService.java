package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import jakarta.validation.Valid;

public interface WorkService {
    int saveWork(@Valid WorkRequestDto workRequestDto);

    WorkResponseDto getWork(Long workId);
}
