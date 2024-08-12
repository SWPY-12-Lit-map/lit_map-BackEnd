package com.lit_map_BackEnd.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("litmap12@gmail.com");
        mailSender.setPassword("amgjqexnoejyecer");
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }

    // 메일 인증서버 정보 가져오기
    private Properties getMailProperties(){
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
       // properties.setProperty("mail.debug", "true");
        //properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        //properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.connectiontimeout", "5000");
        properties.setProperty("mail.smtp.timeout", "5000");
        properties.setProperty("mail.smtp.writetimeout", "5000");

        return properties;
    }
}
