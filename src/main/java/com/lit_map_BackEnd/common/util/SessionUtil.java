package com.lit_map_BackEnd.common.util;

import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Enumeration;

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

    public static void createSessionCookie(HttpSession session, HttpServletResponse response) {
        session.setMaxInactiveInterval((int) Duration.ofDays(1).toSeconds()); // 세션 유효 기간 설정
        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true); // HttpOnly 속성 설정
        sessionCookie.setSecure(true); // HTTPS 사용 시에만 설정
        sessionCookie.setMaxAge((int) Duration.ofDays(1).toSeconds()); // 쿠키 유효 기간 설정
        response.addCookie(sessionCookie); // 응답에 쿠키 추가
    }
}
