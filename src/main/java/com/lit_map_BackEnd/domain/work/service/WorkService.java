package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import jakarta.validation.Valid;

public interface WorkService {
    // 임시 저장, 제출, 수정 모두 같은 서비스 사용
    int saveWork(Member member, @Valid WorkRequestDto workRequestDto);

    WorkResponseDto getWork(Long workId);

    void deleteWork(Member member, Long workId);
    WorkResponseDto getWorkData(Long workId, Double versionNum);

}
