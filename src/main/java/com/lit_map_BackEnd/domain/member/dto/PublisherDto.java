package com.lit_map_BackEnd.domain.member.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublisherDto {

    @NotNull(message = "사업자 번호를 입력해주세요.")
    private Long publisherNumber;

    @NotBlank(message = "출판사 이름을 입력해주세요.")
    private String publisherName;

    @NotBlank(message = "출판사 주소를 입력해주세요.")
    private String publisherAddress;

    @NotBlank(message = "출판사 연락처를 입력해주세요.")
    private String publisherPhoneNumber;

    @NotBlank(message = "대표자 이름을 입력해주세요.")
    private String publisherCeo;

    @NotBlank(message = "litmap 이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String litmapEmail;

    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$", message = "비밀번호는 8자 이상 20자 이하, 영어 소문자와 숫자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호가 일치하지 않습니다.")
    private String confirmPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 8, message = "닉네임은 최대 8자까지 입력 가능합니다.")
    private String nickname;

    private String myMessage;

    private String userImage;

}