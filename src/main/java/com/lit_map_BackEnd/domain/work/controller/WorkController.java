package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.service.VersionService;
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
    private final VersionService versionService;

    @GetMapping("/{id}")
    @Operation(summary = "상세 작품 확인하기", description = "작품의 기본 정보, 모든 버전의 인물 관계도와 캐릭터 전달")
    public ResponseEntity<SuccessResponse> getWork(@PathVariable(name = "id") Long id) {
        WorkResponseDto responseWork = workService.getWork(id);

        SuccessResponse res = SuccessResponse.builder()
                .result(responseWork)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}/{versionNum}")
    @Operation(summary = "특정 버전 가져오기", description = "수정과 추가를 하기 위해 작품의 특정 버전의 정보를 가져오는 API")
    public ResponseEntity<SuccessResponse> getWorkVersion(@PathVariable(name = "id") Long id,
                                                          @PathVariable(name = "versionNum") Double versionNum) {
        // 해당 작품의 특정 버전 정보 가져오기 ( 전체데이터 or 해당 버전의 내용 )
        VersionResponseDto responseVersion = versionService.findVersionByWorkAndNumber(id, versionNum) ;

        SuccessResponse res = SuccessResponse.builder()
                .result(responseVersion)
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

    @DeleteMapping("/{id}")
    @Operation(summary = "작품 삭제", description = "작품을 삭제하고 관련 내용도 같이 삭제")
    public ResponseEntity<SuccessResponse> deleteWork(@PathVariable(name = "id") Long id) {
        workService.deleteWork(id);

        SuccessResponse res = SuccessResponse.builder()
                .result("성공")
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
