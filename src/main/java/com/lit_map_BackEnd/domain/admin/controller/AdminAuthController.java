package com.lit_map_BackEnd.domain.admin.controller;


import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.admin.service.AdminAuthService;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import com.lit_map_BackEnd.domain.work.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;
    private VersionService versionService;
    private WorkService workService;

    @Autowired
    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @GetMapping("/status")
    public String getAdminStatus() {
        if (adminAuthService.isAdmin()) {
            return "Current user is an admin";
        } else {
            return "Current user is not an admin";
        }
    }


    @PutMapping("/confirm/{versionId}")
    @Operation(summary = "관리자 작품 승인", description = "관리자 작품 승인 완료")
    public ResponseEntity<SuccessResponse> confirmVersion(
            @PathVariable(name = "versionId") Long versionId) {
        versionService.approveMail(versionId);
        //에러메시지 수정필요
        SuccessResponse res = SuccessResponse.builder()
                .result("승인 성공")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
