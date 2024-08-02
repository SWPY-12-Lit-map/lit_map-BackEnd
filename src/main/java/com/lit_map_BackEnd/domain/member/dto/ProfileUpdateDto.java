package com.lit_map_BackEnd.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDto {
    private String nickname; // 닉네임
    private String myMessage; // 메시지
    private String userImage; // 사용자 이미지
}