package com.lit_map_BackEnd.domain.mail.dto;

import com.lit_map_BackEnd.domain.work.entity.Work;
import lombok.*;

@Data
@Builder
public class MailWorkDto {
    private String litmapEmail;
    private Work work;

    public MailWorkDto(String litmapEmail, Work work) {
        this.litmapEmail = litmapEmail;
        this.work = work;
    }

    // Getters and Setters
}
