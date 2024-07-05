package com.lit_map_BackEnd.domain.work.dto;

import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class WorkResponseDto {
    private String category;
    private List<String> genre;
    private List<String> author;
    private String imageUrl;
    private String memberName;
    private String publisherName;
    private String title;
    private String contents;
    private List<CastResponseDto> casts;
    private List<VersionResponseDto> versions;
}
