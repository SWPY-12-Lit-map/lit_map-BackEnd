package com.lit_map_BackEnd.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class BusinessVerificationResponse {
    private String status;
    private String message;

    public BusinessVerificationResponse() {
        this.status = "default status";
        this.message = "default message";
    }
}