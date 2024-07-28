package com.lit_map_BackEnd.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublisherResponseDto {

    private Long id;
    private Long publisherNumber;
    private String publisherName;
    private String publisherAddress;
    private String publisherPhoneNumber;
    private String publisherCeo;
    private List<MemberResponseDto> memberList;

}
