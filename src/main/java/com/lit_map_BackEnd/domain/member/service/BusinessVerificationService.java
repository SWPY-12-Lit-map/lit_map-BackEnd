package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationRequest.BusinessVerificationRequestWrapper;
import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BusinessVerificationService {

    @Value("${external.api.business.verification.url}")
    private String verificationApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public BusinessVerificationResponse verifyBusiness(BusinessVerificationRequestWrapper request) throws URISyntaxException {

        URI url = new URI(verificationApiUrl);

        log.info("Request URL: {}", url); // 로그에 URL 출력
        log.info("Request Body: {}", request); // 요청 내용 로깅

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<BusinessVerificationRequestWrapper> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<BusinessVerificationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BusinessVerificationResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error: {}", e.getStatusCode(), e);
            BusinessVerificationResponse errorResponse = new BusinessVerificationResponse();
            errorResponse.setStatus(e.getStatusCode().toString());
            errorResponse.setMessage(e.getResponseBodyAsString());
            return errorResponse;
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            BusinessVerificationResponse errorResponse = new BusinessVerificationResponse();
            errorResponse.setStatus("500");
            errorResponse.setMessage("An unexpected error occurred");
            return errorResponse;
        }
    }
}