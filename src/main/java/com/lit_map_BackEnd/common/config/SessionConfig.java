package com.lit_map_BackEnd.common.config;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@Configuration
public class SessionConfig {
    // 이 설정을 추가하면 HttpOnly 속성이 false 로 설정되어 클라이언트 측 스크립트가 쿠키에 접근가능
    @Bean
    public ServletContextInitializer initializer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(false);
            }
        };
    }
}
