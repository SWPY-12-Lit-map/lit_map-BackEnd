package com.lit_map_BackEnd.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublisherUpdateDto extends MemberUpdateDto{

    @NotBlank(message = "출판사 주소를 입력해주세요.")
    private String publisherAddress; // 출판사 주소

    @NotBlank(message = "출판사 연락처를 입력해주세요.")
    private String publisherPhoneNumber; // 출판사 연락처

    @NotBlank(message = "대표자 이름을 입력해주세요.")
    private String publisherCeo; // 대표자 이름

}
