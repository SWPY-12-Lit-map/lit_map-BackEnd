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
    DUPLICATE_WORK_NAME(500, "중복된 작품 이름이 있습니다"),
    DUPLICATE_CAST_NAME(500, "중복된 캐릭터가 존재합니다"),
    WORK_NOT_FOUND(500, "해당 작품은 존재하지 않습니다"),
    VERSION_NOT_FOUND(500, "해당 버전은 존재하지 않습니다"),
    CATEGORY_NOT_FOUND(500, "해당 카테고리는 존재하지 않습니다"),
    JSON_PARSING_ERROR(500, "JSON 데이터에 문제가 있습니다"),
    WRITER_WRONG(500, "기존의 작성자가 아닙니다"),

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
