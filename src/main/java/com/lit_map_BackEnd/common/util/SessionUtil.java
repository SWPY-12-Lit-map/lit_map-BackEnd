package com.lit_map_BackEnd.common.util;

import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionUtil {

    private final MemberPublisherService memberPublisherService;

    // 세션에서 프로필 정보를 가져오는 메서드
    public Optional<Member> getProfile(HttpServletRequest request) {
        Member loggedInMember = getLoggedInUser(request);
        if (loggedInMember == null) {
            return Optional.empty();
        }

        Member memberProfile = memberPublisherService.findByLitmapEmail(loggedInMember.getLitmapEmail());
        return Optional.of(memberProfile);
    }

    public static HttpSession getSession(HttpServletRequest request) {
        return request.getSession(true); // 새로운 세션을 생성하거나 기존 세션을 반환
    }

    // 세션에 로그인한 사용자 정보를 설정하는 메서드
    public static void setLoggedInUser(HttpServletRequest request, Member member) {
        HttpSession session = getSession(request);
        session.setAttribute("loggedInUser", member);
    }

    // 세션에서 로그인한 사용자 정보를 가져오는 메서드
    public static Member getLoggedInUser(HttpServletRequest request) {
        HttpSession session = getSession(request);
        return (Member) session.getAttribute("loggedInUser");
    }

    // 세션 쿠키를 생성하고 응답에 추가하는 메서드
    public static void createSessionCookie(HttpSession session, HttpServletResponse response) {
        session.setMaxInactiveInterval((int) Duration.ofDays(1).toSeconds()); // 세션 유효 기간 설정
        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true); // HttpOnly 속성 설정
        sessionCookie.setSecure(true); // HTTPS 사용 시에만 설정
        sessionCookie.setMaxAge((int) Duration.ofDays(1).toSeconds()); // 쿠키 유효 기간 설정

        // Set-Cookie 헤더를 사용하여 SameSite=None; Secure 속성 추가
        response.addHeader("Set-Cookie", String.format("JSESSIONID=%s; Path=/; HttpOnly; Secure; SameSite=None", session.getId()));
    }
}
