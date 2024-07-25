package com.lit_map_BackEnd.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MemberUpdateDto {

    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String workEmail; // 업무용 이메일
    private String name; // 이름

    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$", message = "비밀번호는 8자 이상 20자 이하, 영어 소문자와 숫자를 포함해야 합니다.")
    private String password; // 비밀번호
    private String confirmPassword; // 비밀번호 확인

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname; // 닉네임

    private String myMessage; // 메시지
    private String userImage; // 사용자 이미지
    private String urlLink; // 판매 링크 사이트
}
