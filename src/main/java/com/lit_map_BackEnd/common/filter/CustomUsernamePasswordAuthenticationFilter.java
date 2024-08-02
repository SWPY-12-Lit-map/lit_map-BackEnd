package com.lit_map_BackEnd.common.filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 로그인 폼에서 전달된 파라미터를 가져옵니다.
        String username = request.getParameter("litmap_email"); // 사용자 정의 필드 이름
        String password = request.getParameter("password");

        // 인증 요청 생성
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        // 인증 요청을 AuthenticationManager로 전달
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
