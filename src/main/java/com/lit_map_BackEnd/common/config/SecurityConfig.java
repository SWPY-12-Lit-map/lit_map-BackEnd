package com.lit_map_BackEnd.common.config;

import com.lit_map_BackEnd.domain.member.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화

                // 요청에 대한 권한 설정
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        // Swagger 관련 엔드포인트에 대한 접근 허용
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**").permitAll()

                        // 관리자만 접근 가능한 엔드포인트 설정 -> 관리자 완성후 수정하기
                        .requestMatchers("/api/publishers/approve-withdrawal").hasRole(Role.ADMIN.name())

                        // 모든 사용자에게 접근 허용
                        .requestMatchers("/api/members/**").permitAll()
                        .requestMatchers("/api/publishers/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll()
                        .requestMatchers("/main").permitAll()

                        // 승인된 회원, 출판사 회원, 관리자만 특정 엔드포인트에 접근할 수 있도록 설정
                        .requestMatchers("/member/**").hasAnyRole(Role.APPROVED_MEMBER.name(), Role.PUBLISHER_MEMBER.name(), Role.ADMIN.name())

                        // 그 외의 모든 요청에 대해 인증된 사용자만 접근 가능하도록 설정
                        .anyRequest().authenticated()
                )

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //비밀번호 인코더를 Bean으로 정의
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // CORS 설정을 정의하는 메서드
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용되는 Origin 설정 (프론트엔드의 도메인 설정)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:8080"));

        // 허용되는 HTTP 메서드 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용되는 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));

        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 모든 경로에 대해 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
