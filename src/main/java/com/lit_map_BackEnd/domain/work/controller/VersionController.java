package com.lit_map_BackEnd.domain.work.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/version")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @PostMapping("")
    @Operation(summary = "작품 인물 관계도 저장", description = "캐릭터 설정을 마친 후에 인물 관계도를 저장")
    public ResponseEntity<SuccessResponse> saveRelationship(@RequestBody Map<String, Object> jsonMap) {
        try {
            int result = versionService.insertRelationship(jsonMap);
            SuccessResponse res = SuccessResponse.builder()
                    .result(result)
                    .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();

            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new BusinessExceptionHandler(ErrorCode.JSON_PARSING_ERROR);
        }

    }
}
