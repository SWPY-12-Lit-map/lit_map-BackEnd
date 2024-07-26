package com.lit_map_BackEnd.common.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Checking 컨트롤러", description = "AWS 및 배포 통신 API 입니다")
@RestController
@RequestMapping("")
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<SuccessResponse> checkRequest() {

        SuccessResponse res = SuccessResponse.builder()
                .result("2번째 health check 성공")
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
