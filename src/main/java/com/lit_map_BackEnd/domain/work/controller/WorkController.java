package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
@Validated
public class WorkController {

    private final WorkService workService;

    @GetMapping("/{id}")
    @Operation(summary = "상세 작품 확인하기", description = "작품의 기본 정보, 캐릭터들, 인물 관계도까지 전달")
    public ResponseEntity<SuccessResponse> getWork(@PathVariable Long id) {
        WorkResponseDto responseWork = workService.getWork(id);

        SuccessResponse res = SuccessResponse.builder()
                .result(responseWork)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "작품 등록", description = "작품의 데이터 저장")
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
