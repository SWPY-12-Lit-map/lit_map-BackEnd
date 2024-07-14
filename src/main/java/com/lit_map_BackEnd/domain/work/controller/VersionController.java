package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/version")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @DeleteMapping("/{workId}/{versionNum}")
    @Operation(summary = "특정 버전 삭제", description = "특정 버전만 삭제하고 관련 캐릭터들도 같이 삭제")
    public ResponseEntity<SuccessResponse> deleteVersionInWork(@PathVariable Long workId,
                                                               @PathVariable Double versionNum) {
        versionService.deleteVersion(workId, versionNum);

        SuccessResponse res = SuccessResponse.builder()
                .result("성공")
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/rollback/{workId}/{versionNum}")
    @Operation(summary = "버전 수정으로 인한 롤백데이터 저장", description = "버전을 수정하면서 기존의 데이터를 롤백 테이블에 데이터 기입")
    public ResponseEntity<SuccessResponse> updateVersion(@PathVariable Long workId,
                                                         @PathVariable Double versionNum) {
        // 롤백 테이블에 데이터 저장
        versionService.rollBackDataSave(workId, versionNum);

        SuccessResponse res = SuccessResponse.builder()
                .result("롤백 데이터 저장 완료")
                .resultCode(SuccessCode.ROLLBACK_SUCCESS.getStatus())
                .resultMsg(SuccessCode.ROLLBACK_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
