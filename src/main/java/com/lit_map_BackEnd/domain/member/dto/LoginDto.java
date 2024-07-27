package com.lit_map_BackEnd.domain.member.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String litmapEmail;
    private String password;
}
