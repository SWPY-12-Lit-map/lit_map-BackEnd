package com.lit_map_BackEnd.common.exception.code;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 대표예외 처리
    NOT_VALID_ERROR(400, "handle Validation Exception"),
    REQUEST_BODY_MISSING_ERROR(400, "Required request body is missing"),
    NO_RESOURCE_FOUND_ERROR(400, "No static resource"),
    BUSINESS_EXCEPTION_ERROR(500, "business Exception 발생"),

    // service 단 예외 custom
    USER_NOT_FOUND(500, "유저를 찾지 못했습니다"),

    // SQL 문제 발생
    INSERT_ERROR(500, "데이터 삽입 문제 발생"),
    UPDATE_ERROR(500, "데이터 업데이트 문제 발생"),
    DELETE_ERROR(500, "데이터 삭제 문제 발생"),

    ;

    private final int status;
    private final String message;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
}
