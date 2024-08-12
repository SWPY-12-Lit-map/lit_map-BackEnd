package com.lit_map_BackEnd.domain.member.dto;

import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
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
    private String confirmPassword;
    private String urlLink; // 판매 링크 사이트
    private MemberRoleStatus memberRoleStatus = MemberRoleStatus.ACTIVE_MEMBER; // 기본값 설정
}