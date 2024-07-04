package com.lit_map_BackEnd.domain.work.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkRequestDto {
    @NotNull(message = "카테고리를 설정해주세요")
    @NotBlank(message = "카테고리를 설정해주세요")
    private String category;

    @NotNull(message = "장르를 한 개 이상 입력해주세요")
    @NotBlank(message = "장르를 한 개 이상 입력해주세요")
    private String genre;

    @NotNull(message = "작가를 한 명 이상 작성해주세요")
    @NotBlank(message = "작가를 한 명 이상 작성해주세요")
    private String author;

    private String imageUrl;

    @NotNull(message = "사용자를 다시 확인해주세요")
    private Long memberId;

    private String publisherName;

    private String title;

    private String contents;

    @NotNull(message = "version의 숫자를 확인해주세요")
    private Double version;
    private String versionName;
}
