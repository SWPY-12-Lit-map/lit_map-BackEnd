package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationRequest.BusinessVerificationRequestWrapper;
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
    public ResponseEntity<String> verifyBusiness(@RequestBody BusinessVerificationRequestWrapper request) {
        try {
            return businessVerificationService.verifyBusiness(request);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid URI syntax: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
/*
b_no 1098177256
start_dt : 20020401
p_nm : 박상권

일반 인증키
(Encoding)
%2BmsntrcPO90hTQeYGDTU2JwE3JFHhrN%2BO5og1JMt%2BkzGgctd8VpJB7uaYtwIQg%2FYyZxNwt8iZawKy4a3IZISVQ%3D%3D
일반 인증키
(Decoding)
+msntrcPO90hTQeYGDTU2JwE3JFHhrN+O5og1JMt+kzGgctd8VpJB7uaYtwIQg/YyZxNwt8iZawKy4a3IZISVQ==
 */