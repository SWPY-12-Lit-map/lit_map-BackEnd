package com.lit_map_BackEnd.domain.work.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionListDto implements Comparable<VersionListDto>{
    private Double versionNum;
    private String versionName;

    @Override
    public int compareTo(VersionListDto o) {
        return (int) ((this.getVersionNum() * 10) - (o.getVersionNum() * 10));
    }
}
