package com.lit_map_BackEnd.domain.member.dto;

import lombok.Data;

@Data
public class PublisherUpdateDto extends MemberUpdateDto{
    private String publisherName;
    private String publisherAddress;
    private String publisherPhoneNumber;
    private String publisherCeo;

    private String name; // 공통 필드
    private String password; // 공통 필드
    private String confirmPassword; // 공통 필드
    private String nickname; // 공통 필드
    private String userImage; // 공통 필드
    private String myMessage; // 공통 필드

    private Boolean withdrawalRequested = false;  // 기본값 설정

}
