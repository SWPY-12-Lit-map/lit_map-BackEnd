package com.lit_map_BackEnd.domain.relation.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class RelationDto {

    private Long workId;
    private String title;
    private String imageUrl;

}
