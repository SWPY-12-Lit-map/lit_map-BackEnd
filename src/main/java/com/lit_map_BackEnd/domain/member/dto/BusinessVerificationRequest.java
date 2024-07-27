package com.lit_map_BackEnd.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationRequest {
    private String b_no; // 사업자번호 숫자만나열
    private String start_dt; // 개업일자 -> YYYYMMDD 형식
    private String p_nm; // 대표자명

}
