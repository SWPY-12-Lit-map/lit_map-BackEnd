package com.lit_map_BackEnd.domain.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class VersionResponseDto {
    private Double versionNum;
    private String versionName;
    private LocalDateTime updateTime;
    private Map<String, Object> relationship;
}
