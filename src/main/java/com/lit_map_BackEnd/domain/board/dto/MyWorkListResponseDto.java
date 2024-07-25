package com.lit_map_BackEnd.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class MyWorkListResponseDto {
    private int totalCount;
    private List<MyWorkListDto> list;
}
