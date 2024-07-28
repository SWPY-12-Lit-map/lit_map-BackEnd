package com.lit_map_BackEnd.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.time.Duration;

public class SessionUtil {

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
