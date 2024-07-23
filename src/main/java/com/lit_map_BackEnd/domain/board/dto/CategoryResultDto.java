package com.lit_map_BackEnd.domain.board.dto;

import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CategoryResultDto {
    private int count;
    private List<WorkResponseDto> works;

    public void countUp() {
        this.count += 1;
    }
}
