package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.member.repository.PublisherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final EmailService emailService;

    @Value("${external.api.publisher.url}")
    private String publisherApiUrl;

    @Override
    @Transactional
    public void requestWithdrawalAll(Long publisherId) {
        approveWithdrawalAll(publisherId);
    }

    @Override
    @Transactional
    public void requestWithdrawalSpecific(Long publisherId, Long memberId) {
        approveWithdrawalSpecific(publisherId, memberId);
    }

    @Override
    @Transactional
    public void approveWithdrawalAll(Long publisherId) {
        Optional<Publisher> publisherOptional = publisherRepository.findById(publisherId);
        if (publisherOptional.isPresent()) {
            Publisher publisher = publisherOptional.get();
            publisherRepository.delete(publisher);

            for (Member member : publisher.getMemberList()) {
                String email = member.getLitmapEmail();
                String subject = "탈퇴 승인 완료";
                String text = "회원님의 탈퇴 요청이 승인되었습니다.";
                emailService.sendEmail(email, subject, text);
            }
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void approveWithdrawalSpecific(Long publisherId, Long memberId) {
        Optional<Publisher> publisherOptional = publisherRepository.findById(publisherId);
        if (publisherOptional.isPresent()) {
            Publisher publisher = publisherOptional.get();
            Member requestedMember = null;

            for (Member member : publisher.getMemberList()) {
                if (member.getId().equals(memberId)) {
                    requestedMember = member;
                    break;
                }
            }

            if (requestedMember != null) {
                String email = requestedMember.getLitmapEmail();
                String subject = "탈퇴 승인 완료";
                String text = "회원님의 탈퇴 요청이 승인되었습니다.";
                emailService.sendEmail(email, subject, text);
            } else {
                throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
            }

            publisherRepository.delete(publisher);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    }
}

/**
데이터포맷	JSON+XML
Base URL	api.odcloud.kr/api
Swagger URL	https://infuser.odcloud.kr/oas/docs?namespace=15060743/v1

일반 인증키(Encoding)
%2BmsntrcPO90hTQeYGDTU2JwE3JFHhrN%2BO5og1JMt%2BkzGgctd8VpJB7uaYtwIQg%2FYyZxNwt8iZawKy4a3IZISVQ%3D%3D
일반 인증키(Decoding)
+msntrcPO90hTQeYGDTU2JwE3JFHhrN+O5og1JMt+kzGgctd8VpJB7uaYtwIQg/YyZxNwt8iZawKy4a3IZISVQ==
 */