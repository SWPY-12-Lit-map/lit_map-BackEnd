package com.lit_map_BackEnd.domain.work.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime publisherDate;
    private String imageUrl;
    private String memberName;
    private String publisherName;
    private String title;
    private String contents;
    private VersionResponseDto versions;
    // 한가지 작품 반환시 사용
    private List<VersionListDto> versionList;
}
