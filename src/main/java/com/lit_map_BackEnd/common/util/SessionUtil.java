package com.lit_map_BackEnd.common.util;

import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionUtil {

    private final MemberPublisherService memberPublisherService;

    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우 401 응답
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 올바른 사용자 정보가 아닌 경우 401 응답
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String litmapEmail = userDetails.getUsername();
        Member memberProfile = memberPublisherService.findByLitmapEmail(litmapEmail);

        return new ResponseEntity<>(memberProfile, HttpStatus.OK); // 사용자 프로필 반환
    }
}
