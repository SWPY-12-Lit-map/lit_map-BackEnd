package com.lit_map_BackEnd.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank(message = "litmap 이메일을 입력해주세요.")
    private String litmapEmail;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
