package com.lit_map_BackEnd.domain.character.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.character.repository.CastRepository;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CastServiceImpl implements CastService {

    private final CastRepository castRepository;
    private final WorkRepository workRepository;

    @Override
    @Transactional
    public int insertCharacter(List<CastRequestDto> castRequestDto) {
        for (CastRequestDto requestDto : castRequestDto) {
            Work work = workRepository.findById(requestDto.getWorkId())
                    .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

            Cast cast = Cast.builder()
                    .name(requestDto.getName())
                    .work(work)
                    .imageUrl(requestDto.getImageUrl())
                    .type(requestDto.getType())
                    .role(requestDto.getRole())
                    .gender(requestDto.getGender())
                    .age(requestDto.getAge())
                    .mbti(requestDto.getMbti())
                    .contents(requestDto.getContents())
                    .build();

            castRepository.save(cast);
        }

        return 1;
    }

    @Override
    public List<CastResponseDto> findCharacterByWork(Work work) {
        return castRepository.findByWork(work).stream()
                .map(cast -> CastResponseDto.builder()
                        .name(cast.getName())
                        .imageUrl(cast.getImageUrl())
                        .type(cast.getType())
                        .role(cast.getRole())
                        .gender(cast.getGender())
                        .age(cast.getAge())
                        .mbti(cast.getMbti())
                        .contents(cast.getContents())
                        .build())
                .collect(Collectors.toList());
    }
}
