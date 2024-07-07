package com.lit_map_BackEnd.domain.character.dto;

import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
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

    private Work work;
    private Version version;

    private String imageUrl;

    @NotNull(message = "종족을 선택해주세요")
    @NotBlank(message = "종족을 선택해주세요")
    private String type;

    private String role;

    private String gender;

    private int age;
    private String mbti;
    private String contents;

    public void setWork(Work work) {
        this.work = work;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
