package com.lit_map_BackEnd.domain.mail.controller;

import com.lit_map_BackEnd.domain.mail.dto.MailDto;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@ComponentScan("controllers")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }
/*
    @GetMapping("/mail/send")
    public String main() {
        return "SendMail.html";
    }
*/
    @GetMapping("/mail/send")
    public String sendMail(MailDto mailDto) {
        mailService.sendMail(mailDto);
        System.out.println("메일 전송 완료");
      //  return ResponseEntity.ok("메일 전송 완료");
         return "AfterMail"; //.html
    }


}