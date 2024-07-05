package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @PostMapping("")
    @Operation(summary = "작품 1차 등록", description = "작품의 기본 설정만 한 것을 데이터로 전송")
    public ResponseEntity<SuccessResponse> saveWork(@RequestBody @Valid WorkRequestDto workRequestDto) {
        int result = workService.saveWork(workRequestDto);

        SuccessResponse res = SuccessResponse.builder()
                .result(result)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
