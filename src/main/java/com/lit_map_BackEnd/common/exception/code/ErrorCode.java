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
    IMAGE_NOT_FOUND(500, "해당 사진은 존재하지 않습니다"),
    VERSION_NOT_FOUND(500, "해당 버전은 존재하지 않습니다"),
    CATEGORY_NOT_FOUND(500, "해당 카테고리는 존재하지 않습니다"),
    GENRE_NOT_FOUND(500, "해당 장르는 존재하지 않습니다"),
    JSON_PARSING_ERROR(500, "JSON 데이터에 문제가 있습니다"),
    WRITER_WRONG(500, "기존의 작성자가 아닙니다"),
    WRITER_INVALID(500, "작성 권한이 없는 회원입니다"),
    NOT_CONFIRM_VERSION(500, "아직 승인이 난 버전이 아닙니다"),
    FILE_CONVERT_ERROR(500, "파일 변환에 실패하였습니다"),
    INVALID_FILE_FORMAT(500, "해당 파일 타입은 지원하지 않습니다"),
    ENCODING_ERROR(500, "지원하지 않는 문자 인코딩입니다"),

    DUPLICATE_EMAIL(500,"이미 존재하는 이메일입니다."),
    DUPLICATE_WORK_EMAIL(500,"이미 존재하는 업무용 이메일입니다."),
    DUPLICATE_EMAILS(500,"litmap 이메일과 업무용 이메일이 동일할 수 없습니다."),
    PASSWORDS_DO_NOT_MATCH(500,"비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT(500,"비밀번호 형식이 올바르지 않습니다."),
    PROHIBITED_NICKNAME(500,"허용되지 않는 닉네임입니다."),
    INVALID_USER_INFO(500,"사용자 정보가 일치하지 않습니다."),
    PENDING_USER(500,"승인 대기 중인 회원입니다"),
    WITHDRAWN_USER(500,"탈퇴한 회원입니다"),
    DUPLICATE_PUBLISHER(400, "이미 존재하는 출판사입니다."),
    PUBLISHER_NOT_FOUND(404,"출판사를 찾을 수 없습니다."),
    EMAIL_SEND_FAILED(500,"이메일로 임시비밀번호 발송에 실패했습니다."),  // 수정된 부분
    APPROVE_ERROR(500, "승인 실패했습니다."),

    // SQL 문제 발생
    INSERT_ERROR(500, "데이터 삽입 문제 발생"),
    UPDATE_ERROR(500, "데이터 업데이트 문제 발생"),
    DELETE_ERROR(500, "데이터 삭제 문제 발생"),
    // 권한 문제
    FORBIDDEN_ERROR(500, "관리자 권한이 아닙니다."),




    ;

    private final int status;
    private final String message;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
}
