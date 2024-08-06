package com.lit_map_BackEnd.domain.work.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import com.lit_map_BackEnd.domain.work.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/version")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;
    private final WorkService workService;

    @DeleteMapping("/{workId}/{versionNum}")
    @Operation(summary = "특정 버전 삭제", description = "특정 버전만 삭제하고 관련 캐릭터들도 같이 삭제")
    public ResponseEntity<SuccessResponse> deleteVersionInWork(HttpServletRequest request,
                                                               @PathVariable(name = "workId") Long workId,
                                                               @PathVariable(name = "versionNum") Double versionNum) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        versionService.deleteVersion(loggedInUser, workId, versionNum);

        SuccessResponse res = SuccessResponse.builder()
                .result("성공")
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/rollback/{workId}/{versionNum}")
    @Operation(summary = "수정하기 버튼", description = "버전을 수정하면서 기존의 데이터를 롤백 테이블에 데이터 기입")
    public ResponseEntity<SuccessResponse> updateVersion(HttpServletRequest request,
                                                         @PathVariable(name = "workId") Long workId,
                                                         @PathVariable(name = "versionNum") Double versionNum) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        // 롤백 테이블에 데이터 저장
        versionService.rollBackDataSave(loggedInUser.getId(), workId, versionNum);
        // 데이터를 반환
        WorkResponseDto workData = workService.getWorkData(workId, versionNum);

        SuccessResponse res = SuccessResponse.builder()
                .result(workData)
                .resultCode(SuccessCode.ROLLBACK_SUCCESS.getStatus())
                .resultMsg(SuccessCode.ROLLBACK_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PutMapping("/confirm/{versionId}")  //작품승인
    @Operation(summary = "관리자 작품 승인", description = "관리자 작품 승인 완료")
    public ResponseEntity<SuccessResponse> confirmVersion(@PathVariable(name = "versionId") Long versionId,
                                                          HttpServletRequest request) {
        versionService.approveMail(versionId, request);

        //에러메시지 수정필요
        SuccessResponse res = SuccessResponse.builder()
                .result("승인 성공")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    //버전 삭제 사유  //@RequestParam String email
    @PostMapping("/confirm/decline")
    @Operation(summary = "관리자 작품 반려", description = "관리자 작품 반려 완료 및 사유")
    public ResponseEntity<SuccessResponse> sendSummaryEmail(Long versionId, @RequestParam String summary,  HttpServletRequest request) {
        versionService.declineMail(versionId, summary, request); //service 지정후 impl 수정
        SuccessResponse res = SuccessResponse.builder()
                .result("승인 성공")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();

        return ResponseEntity.ok(res);


    }
}
