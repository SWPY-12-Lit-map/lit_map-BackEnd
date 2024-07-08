package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
