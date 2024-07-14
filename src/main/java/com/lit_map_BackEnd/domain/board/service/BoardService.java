package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;

import java.util.List;

public interface BoardService {
    List<ConfirmListDto> getConfirmData();
}
