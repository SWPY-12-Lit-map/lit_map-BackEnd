package com.lit_map_BackEnd.domain.character.service;


import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.work.entity.Work;

import java.util.List;

public interface CastService {
    Cast insertCharacter(CastRequestDto castRequestDto);

    List<CastResponseDto> findCharacterByWork(Work work);
}
