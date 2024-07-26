package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
        //    message.setFrom("sooho7767@naver.com");  // 발신자 이메일 고정
            message.setTo(to);  // 수신자 이메일
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

//    @Override
//    public void sendEmail(String to, String subject, String text) {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text, true); // true to enable HTML content
//            helper.setFrom("sooho7767@naver.com"); // 발신자 이메일 설정
//            mailSender.send(mimeMessage);
//        } catch (Exception e) {
//            e.printStackTrace(); // 예외를 로깅
//            throw new BusinessExceptionHandler(ErrorCode.EMAIL_SEND_FAILED);
//        }
//    }
}
