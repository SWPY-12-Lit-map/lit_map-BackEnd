package com.lit_map_BackEnd.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SearchDto {
    private SearchType searchType;
    private String question;
}
