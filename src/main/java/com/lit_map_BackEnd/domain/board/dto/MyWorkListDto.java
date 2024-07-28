package com.lit_map_BackEnd.domain.board.dto;

import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class MyWorkListDto {
    private Long workId;
    private String title;
    private String category;
    private String mainAuthor;
    private String publisher;
    @Builder.Default
    private List<VersionListDto> versionLists = new ArrayList<>();
}
