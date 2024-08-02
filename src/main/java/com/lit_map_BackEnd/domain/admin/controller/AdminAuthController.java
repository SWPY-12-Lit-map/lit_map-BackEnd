package com.lit_map_BackEnd.domain.admin.controller;


import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.admin.service.AdminAuthService;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import com.lit_map_BackEnd.domain.work.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;
    private VersionService versionService;
    private WorkService workService;

    @PostMapping("/status")
    public ResponseEntity<String> getAdminStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("인증정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Authentication");
        }

        System.out.println("Authorities: " + authentication.getAuthorities());

        boolean isAdmin = adminAuthService.isAdmin();
        if (isAdmin) {
            System.out.println("관리자계정입니다");
            return ResponseEntity.ok(SuccessCode.SELECT_SUCCESS.getMessage());
        } else {
            System.out.println("관리자계정이 아닙니다");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자계정아님");
        }
    }

}
