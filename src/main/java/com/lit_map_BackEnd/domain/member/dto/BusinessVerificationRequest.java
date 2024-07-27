package com.lit_map_BackEnd.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationRequest {
    private String b_no; // 사업자번호 숫자만 나열
    private String start_dt; // 개업일자 -> YYYYMMDD 형식
    private String p_nm; // 대표자명
    private String p_nm2; // 부대표자명
    private String b_nm; // 사업자명
    private String corp_no; // 법인등록번호
    private String b_sector; // 업종
    private String b_type; // 업태
    private String b_adr; // 주소

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessVerificationRequestWrapper {
        private List<BusinessVerificationRequest> businesses;
    }
}

/**
 * b_no 1098177256
 * start_dt : 20020401
 * p_nm : 박상권
 * 테스트용
 */