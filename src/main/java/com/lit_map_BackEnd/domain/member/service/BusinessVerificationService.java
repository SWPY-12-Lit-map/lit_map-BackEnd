package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationRequest;
import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@Service
public class BusinessVerificationService {
    // 사업자번호 진위확인 api

    @Value("${external.api.business.verification.url}")
    private String verificationApiUrl;

    @Value("${external.api.business.verification.serviceKey}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    public BusinessVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BusinessVerificationResponse verifyBusiness(BusinessVerificationRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(verificationApiUrl)
                .queryParam("serviceKey", serviceKey)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<BusinessVerificationRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<BusinessVerificationResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                BusinessVerificationResponse.class
        );

        return response.getBody();
    }
}
