package com.lit_map_BackEnd.common.exception;

import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessExceptionHandler extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessExceptionHandler(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
