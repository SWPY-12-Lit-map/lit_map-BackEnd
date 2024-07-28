package com.lit_map_BackEnd.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String litmapEmail;
    private String workEmail;
    private String name;
    private String nickname;
    private String myMessage;
    private String userImage;
    private String urlLink;
    private String memberRoleStatus;
}
