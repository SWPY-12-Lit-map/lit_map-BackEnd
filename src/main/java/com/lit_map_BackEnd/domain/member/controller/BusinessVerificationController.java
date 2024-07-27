package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationRequest;
import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationResponse;
import com.lit_map_BackEnd.domain.member.service.BusinessVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessVerificationController {

    private final BusinessVerificationService businessVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<BusinessVerificationResponse> verifyBusiness(@RequestBody BusinessVerificationRequest request) {
        BusinessVerificationResponse response = businessVerificationService.verifyBusiness(request);
        return ResponseEntity.ok(response);
    }
}