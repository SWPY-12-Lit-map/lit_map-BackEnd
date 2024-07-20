package com.lit_map_BackEnd.domain.work.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lit_map_BackEnd.domain.youtube.entity.Youtube;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkResponseDto {
    private Long workId;
    private String category;
    private List<String> genre;
    private List<String> author;
    private String imageUrl;
    private String memberName;
    private String publisherName;
    private String title;
    private String contents;
    private VersionResponseDto versions;
    //private List<CastResponseDto> casts;

    private List<Youtube> youtubes;
}
