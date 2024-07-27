package com.lit_map_BackEnd.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            // MimeMessageHelper를 사용하여 HTML 설정
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true: HTML 모드
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
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
