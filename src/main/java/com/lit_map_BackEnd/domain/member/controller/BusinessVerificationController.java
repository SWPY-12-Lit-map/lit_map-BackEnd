package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationRequest.BusinessVerificationRequestWrapper;
import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationResponse;
import com.lit_map_BackEnd.domain.member.service.BusinessVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessVerificationController {

    private final BusinessVerificationService businessVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<BusinessVerificationResponse> verifyBusiness(@RequestBody BusinessVerificationRequestWrapper request) {
        try {
            BusinessVerificationResponse response = businessVerificationService.verifyBusiness(request);
            return ResponseEntity.ok(response);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BusinessVerificationResponse("400", "Invalid URI syntax: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BusinessVerificationResponse("500", "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
