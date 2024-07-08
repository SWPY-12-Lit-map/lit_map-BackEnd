package com.lit_map_BackEnd.common.exception;

import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.response.ErrorResponse;
import com.lit_map_BackEnd.common.exception.response.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final HttpStatus HTTP_STATUS_OK = HttpStatus.OK;

    /*
     * 400 관련 에러
     * 백엔드가 의도치 않게 발생하는 에러 관리
     */

    // 유효성에 문제가 발생
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (int i = 0; i < fieldErrors.size(); i++) {
            FieldError fieldError = fieldErrors.get(i);
            stringBuilder.append(fieldError.getField()).append(" : ");
            stringBuilder.append(fieldError.getDefaultMessage());

            if (i < fieldErrors.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, String.valueOf(stringBuilder));
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    // 리스트에서 발생
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException ex) {
        List<ValidationError> validationErrors = new ArrayList<>();
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        for (ConstraintViolation<?> x : constraintViolations) {
            String targetName = "";
            int idx = 0;
            for (Path.Node node : x.getPropertyPath()) {
                if (node.getKind().equals(ElementKind.PROPERTY)) {
                    idx = node.getIndex();
                    targetName = node.getName();
                }
            }
            String message = x.getMessage();
            ValidationError error = new ValidationError();
            error.setIndex(idx + 1);
            error.setField(targetName);
            error.setDefaultMessage(message);
            validationErrors.add(error);
        }

        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, validationErrors);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 넘어온 request에서 body 값이 존재하지 않은 문제
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, "바디에 객체가 담겨있지 않습니다");
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    // URI가 잘못 되었을때
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundException(NoResourceFoundException ex) {
        log.error("NoResourceFoundException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NO_RESOURCE_FOUND_ERROR, "잘못된 URI 경로입니다.");
        return new ResponseEntity<>(response, HTTP_STATUS_OK);
    }

    /*
     * 500관련 에러
     * 사용법 : Service에 문제 발생 시 BusinessExceptionHandler(ErrorCode)
     */
    @ExceptionHandler(BusinessExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleCustomException(BusinessExceptionHandler ex) {
        log.debug("business Exception 발생");
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BUSINESS_EXCEPTION_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
