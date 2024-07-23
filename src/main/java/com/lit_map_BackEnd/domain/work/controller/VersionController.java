package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.work.entity.Confirm;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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


    @PutMapping("/{versionId}/confirm")
    @Operation(summary = "관리자 승인", description = "관리자 승인 완료")
    public ResponseEntity<?> confirmVersion(@PathVariable Long versionId,
                                            @RequestParam Confirm confirm,
                                            Authentication authentication) {

        //에러메시지 수정필요
        try {
            Version confirmedVersion = versionService.confirmVersion(versionId, confirm, authentication);
            return ResponseEntity.ok(confirmedVersion);
        } catch (EntityNotFoundException e) { //추가
            throw new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND);
            //return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) { //추가
            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
            //return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
