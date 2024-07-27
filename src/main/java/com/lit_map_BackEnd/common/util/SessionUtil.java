package com.lit_map_BackEnd.common.util;

import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionUtil {

    private final MemberPublisherService memberPublisherService;
    private final HttpSession session;

    public ResponseEntity<?> getProfile() {
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("loggedInUser");
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String litmapEmail = userDetails.getUsername();
        Member memberProfile = memberPublisherService.findByLitmapEmail(litmapEmail);

        return new ResponseEntity<>(memberProfile, HttpStatus.OK);
    }
}
