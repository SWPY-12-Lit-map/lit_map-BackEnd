package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
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

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    @Value("${external.api.publisher.url}")
    private String publisherApiUrl;

    @Override
    @Transactional
    public void requestPublisherWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setRoleStatus(MemberRoleStatus.WITHDRAWN_MEMBER);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void approvePublisherWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setRoleStatus(MemberRoleStatus.UNKNOWN_MEMBER);
        memberRepository.save(member);

        String subject = "탈퇴가 승인되었습니다.";
        String content = "회원님의 탈퇴 요청이 승인되었습니다. 그동안 이용해주셔서 감사합니다.";
        emailService.sendEmail(member.getLitmapEmail(), subject, content);
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