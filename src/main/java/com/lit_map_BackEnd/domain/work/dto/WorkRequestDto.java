package com.lit_map_BackEnd.domain.work.dto;

import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class WorkRequestDto {
    private String category;

    private String genre;

    private String author;

    private String imageUrl;

    @NotNull(message = "사용자를 다시 확인해주세요")
    private Long memberId;

    @NotNull(message = "출판사를 입력해주세요")
    private String publisherName;

    @NotNull(message = "제목을 입력해주세요")
    private String title;

    private String contents;

    @NotNull(message = "version의 숫자를 확인해주세요")
    private Double version;

    private String versionName;

    @Valid
    @NotNull(message = "캐릭터의 정보를 입력해주세요")
    private List<CastRequestDto> casts;
    private Map<String, Object> relationship;
}
