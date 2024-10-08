package com.lit_map_BackEnd.domain.work.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lit_map_BackEnd.domain.work.entity.Confirm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionListDto implements Comparable<VersionListDto>{
    private Long versionId;
    private Double versionNum;
    private String versionName;
    private LocalDateTime lastUpdateDate;
    private Confirm confirm;

    @Override
    public int compareTo(VersionListDto o) {
        return (int) ((this.getVersionNum() * 10) - (o.getVersionNum() * 10));
    }
}
