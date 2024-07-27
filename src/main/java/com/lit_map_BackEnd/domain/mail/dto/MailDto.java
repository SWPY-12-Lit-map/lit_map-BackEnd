package com.lit_map_BackEnd.domain.mail.dto;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {
    private String email; // 받는 사람의 이메일 (litmapEmail)
    private String address; // 임시 비밀번호를 발송할 이메일
    private String title;
    private String message;
}
