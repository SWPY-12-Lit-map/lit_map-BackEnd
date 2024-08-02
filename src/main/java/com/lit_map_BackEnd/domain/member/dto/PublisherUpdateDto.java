package com.lit_map_BackEnd.domain.member.dto;

import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PublisherUpdateDto {

    private String publisherAddress; // 출판사 주소
    private String publisherPhoneNumber; // 출판사 연락처
    private String publisherCeo; // 대표자 이름

    private String name; // 이름
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$", message = "비밀번호는 8자 이상 20자 이하, 영어 소문자와 숫자를 포함해야 합니다.")
    private String password; // 비밀번호
    @NotBlank(message = "비밀번호가 일치하지 않습니다.")
    private String confirmPassword;

    private MemberRoleStatus memberRoleStatus;

}