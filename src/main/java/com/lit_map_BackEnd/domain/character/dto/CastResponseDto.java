package com.lit_map_BackEnd.domain.character.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CastResponseDto {
    private String name;
    private String imageUrl;
    private String type;
    private String role;
    private String gender;
    private int age;
    private String mbti;
    private String contents;
}
