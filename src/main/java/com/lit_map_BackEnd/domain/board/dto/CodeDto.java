package com.lit_map_BackEnd.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "템플릿")
public class CodeDto {

    @Size(min = 6, message = "6글자 이상으로 입력해주세요")
    @Schema(description = "템플릿 이름")
    private String name;

    @Min(value = 15, message = "15살 이상이여야 합니다")
    @Max(value = 30, message = "30살 이하이여야 합니다")
    @Schema(description = "템플릿 나이")
    private int age;
}
