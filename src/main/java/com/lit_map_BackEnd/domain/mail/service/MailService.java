package com.lit_map_BackEnd.domain.mail.service;

public interface MailService {

    void sendEmail(String to, String subject, String text);
}
