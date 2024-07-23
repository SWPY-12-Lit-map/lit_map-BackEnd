package com.lit_map_BackEnd.domain.work.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionResponseDto {
    private Double versionNum;
    private String versionName;
    private LocalDateTime updateTime;
    private List<CastResponseDto> casts;
    private Map<String, Object> relationship;
}
