package com.lit_map_BackEnd.domain.work.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkResponseDto {
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
}
