package com.lit_map_BackEnd.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindPublisherEmailDto { //출판사직원 아이디찾기
    private Long publisherNumber; //사업자번호
    private String publisherName; //출판사이름
    private String memberName; //이름
}
