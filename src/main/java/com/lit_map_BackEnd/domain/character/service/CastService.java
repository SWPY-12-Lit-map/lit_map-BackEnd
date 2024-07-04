package com.lit_map_BackEnd.domain.character.service;


import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;

import java.util.List;

public interface CastService {
    int insertCharacter(List<CastRequestDto> castRequestDto);
}
