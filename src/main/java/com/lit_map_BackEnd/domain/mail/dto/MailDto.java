package com.lit_map_BackEnd.domain.mail.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
@AllArgsConstructor
public class MailDto {
    private String address;
    private String title;
    private String content;
}