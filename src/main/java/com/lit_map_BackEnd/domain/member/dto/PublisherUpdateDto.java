package com.lit_map_BackEnd.domain.member.dto;

import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PublisherUpdateDto extends MemberUpdateDto{

    private String publisherAddress; // 출판사 주소
    private String publisherPhoneNumber; // 출판사 연락처
    private String publisherCeo; // 대표자 이름

    private MemberRoleStatus memberRoleStatus;

}
