package com.lit_map_BackEnd.common.exception.response;

import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;                        // 에러 상태 코드
    private String resultMsg;                  // 에러 메시지
    private String reason;                     // 에러 이유
    private List<ValidationError> listErrors;  // 리스트에서 에러 받기

    @Builder
    protected ErrorResponse(final ErrorCode code, final String reason) {
        this.status = code.getStatus();
        this.resultMsg = code.getMessage();
        this.reason = reason;
    }

    @Builder
    protected ErrorResponse(final ErrorCode code, final List<ValidationError> errors) {
        this.status = code.getStatus();
        this.resultMsg = code.getMessage();
        this.listErrors = errors;
    }

    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }

    public static ErrorResponse of(final ErrorCode code, final List<ValidationError> errors) {
        return new ErrorResponse(code, errors);
    }
}
