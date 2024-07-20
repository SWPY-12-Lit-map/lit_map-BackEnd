package com.lit_map_BackEnd.common.config;

import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
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

                        // 관리자만 접근 가능한 엔드포인트 설정
                        .requestMatchers("/api/publishers/approve-withdrawal").hasRole(MemberRoleStatus.ADMIN.name())

                        // 모든 사용자에게 접근 허용
                        .requestMatchers("/api/members/**").permitAll()
                        .requestMatchers("/api/publishers/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll()
                        .requestMatchers("/main").permitAll()

                        // 로그인한 회원만 접근 가능한 엔드포인트 설정
                        .requestMatchers("/member/**").hasAnyRole(MemberRoleStatus.ACTIVE_MEMBER.name(), MemberRoleStatus.PUBLISHER_MEMBER.name(), MemberRoleStatus.ADMIN.name())

                        // 탈퇴한 회원은 마이페이지, 관리자 페이지 제외하고 접근 가능
                        .requestMatchers("/member/**").hasAnyRole(MemberRoleStatus.WITHDRAWN_MEMBER.name(), MemberRoleStatus.ADMIN.name())

                        // 그 외의 모든 요청에 대해 인증된 사용자만 접근 가능하도록 설정
                        .anyRequest().authenticated()
                )

                // 세션 관리 설정
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)  // 최대 세션 수를 1로 설정
                        .maxSessionsPreventsLogin(false)  // 새로운 로그인 시 이전 세션 만료
                        .sessionRegistry(sessionRegistry())  // 세션 레지스트리 설정
                )

                // 로그인 설정
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // 로그인 페이지 설정
                        .permitAll()  // 로그인 페이지는 누구나 접근 가능
                        .defaultSuccessUrl("/main")  // 로그인 성공 시 이동할 기본 URL 설정
                        .successHandler((request, response, authentication) -> {
                            MemberRoleStatus role = ((CustomUserDetails) authentication.getPrincipal()).getMember().getMemberRoleStatus();
                            if (role == MemberRoleStatus.ADMIN) {
                                response.sendRedirect("/admin");
                            } else if (role == MemberRoleStatus.PUBLISHER_MEMBER || role == MemberRoleStatus.ACTIVE_MEMBER) {
                                response.sendRedirect("/mypage");
                            } else {
                                response.sendRedirect("/main");
                            }
                        })
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 인코더를 Bean으로 정의
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
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

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
    }

    @Bean
    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
