package com.lit_map_BackEnd.domain.character.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CastRequestDto {
    @NotNull(message = "이름을 입력해주세요")
    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotNull(message = "작품이 선택되지 않았습니다")
    private Long workId;

    private String imageUrl;

    @NotNull(message = "종족을 선택해주세요")
    @NotBlank(message = "종족을 선택해주세요")
    private String type;

    private String role;

    private String gender;

    private int age;
    private String mbti;
    private String contents;
}
