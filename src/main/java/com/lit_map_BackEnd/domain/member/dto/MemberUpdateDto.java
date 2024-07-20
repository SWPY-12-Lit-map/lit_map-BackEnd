package com.lit_map_BackEnd.domain.member.dto;

import lombok.Data;

@Data
public class MemberUpdateDto {
    private String workEmail;
    private String name;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String userImage;
    private String myMessage;
}
