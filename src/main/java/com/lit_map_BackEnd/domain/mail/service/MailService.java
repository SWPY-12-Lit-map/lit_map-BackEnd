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
       /*
       //test
        message.setTo("litmap12@gmail.com"); //litmap12@gmail.com
        message.setSubject("[litmap] 작품 승인");
        message.setText("작품이 승인되었습니다.");  */
        mailSender.send(message);
    }
}