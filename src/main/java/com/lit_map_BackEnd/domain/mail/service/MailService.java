package com.lit_map_BackEnd.domain.mail.service;

import com.lit_map_BackEnd.domain.mail.dto.MailDto;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService { //메일보내기 테스트용 (삭제 가능)

    private JavaMailSender mailSender;

    public void sendMail(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
       // message.setFrom("litmap12@gmail.com");
       message.setTo(mailDto.getAddress());
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getContent());
        mailSender.send(message);
    }
}