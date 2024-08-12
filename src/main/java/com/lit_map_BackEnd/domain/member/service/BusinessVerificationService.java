package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.BusinessVerificationRequest.BusinessVerificationRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@Service
@Slf4j
public class BusinessVerificationService {

    @Value("${external.api.business.verification.url}")
    private String verificationApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> verifyBusiness(BusinessVerificationRequestWrapper request) throws URISyntaxException {
        String urlWithKey = verificationApiUrl; // 이미 서비스 키가 포함된 URL

        URI url = new URI(urlWithKey);

        log.info("Request URL: {}", url); // 로그에 URL 출력
        log.info("Request Body: {}", request); // 요청 내용 로깅

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<BusinessVerificationRequestWrapper> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            log.info("Response Status: {}", response.getStatusCode()); // 응답 상태 코드 로그
            log.info("Response Body: {}", response.getBody()); // 응답 바디 로그
            return response;
        } catch (HttpClientErrorException e) {
            log.error("HTTP error: {}", e.getStatusCode(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}
