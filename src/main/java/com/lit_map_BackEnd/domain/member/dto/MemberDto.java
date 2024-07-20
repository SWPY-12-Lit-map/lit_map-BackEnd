package com.lit_map_BackEnd.domain.member.dto;

import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class MemberDto {

    @NotBlank(message = "litmap 이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String litmapEmail;

    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String workEmail;

    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$", message = "비밀번호는 8자 이상 20자 이하, 영어 소문자와 숫자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호가 일치하지 않습니다.")
    private String confirmPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 8, message = "닉네임은 최대 8자까지 입력 가능합니다.")
    private String nickname;

    private String myMessage; // 마이페이지 변경가능

    private String userImage; // 마이페이지 변경가능

    @NotBlank(message = "판매사이트를 입력해주세요.")
    private String urlLink;

    private MemberRoleStatus memberRoleStatus;

}