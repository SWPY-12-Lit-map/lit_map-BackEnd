package com.lit_map_BackEnd.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublisherMemberRequestDto {
    // 회원가입, 로그인, 비밀번호 찾기 공용
    private PublisherDto publisherDto;
    private MemberDto memberDto;
}
