package com.lit_map_BackEnd.domain.character.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.character.service.CastService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cast")
@RequiredArgsConstructor
public class CastController {

    private final CastService castService;

    @DeleteMapping("/{workId}/{versionNum}/{castName}")
    @Operation(summary = "특정 캐릭터 삭제", description = "혹여 캐릭터를 삭제할때 이미 데이터베이스에 들어가있는것을 대비하여 삭제할 경우 삭제 API 필요")
    public ResponseEntity<SuccessResponse> deleteCastInVersion(@PathVariable(name = "workId") Long workId,
                                                               @PathVariable(name = "versionNum") Double versionNum,
                                                               @PathVariable(name = "castName") String castName) {

        castService.deleteCastInVersion(workId, versionNum, castName);

        SuccessResponse res = SuccessResponse.builder()
                .result("삭제 성공")
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
