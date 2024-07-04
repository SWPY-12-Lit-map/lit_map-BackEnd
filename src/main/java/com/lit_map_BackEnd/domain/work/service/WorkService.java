package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;

public interface WorkService {
    int saveWork(WorkRequestDto workRequestDto);
}
