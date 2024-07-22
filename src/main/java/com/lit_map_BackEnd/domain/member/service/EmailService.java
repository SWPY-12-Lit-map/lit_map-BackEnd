package com.lit_map_BackEnd.domain.member.service;

public interface EmailService {

    // 비밀번호 찾기 - 임시 메일 발송
    void sendEmail(String to, String subject, String text);
}
