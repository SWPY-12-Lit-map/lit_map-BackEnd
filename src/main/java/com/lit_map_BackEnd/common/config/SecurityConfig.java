package com.lit_map_BackEnd.common.config;

import com.lit_map_BackEnd.common.filter.CustomUsernamePasswordAuthenticationFilter;
import com.lit_map_BackEnd.domain.admin.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
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

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CustomUsernamePasswordAuthenticationFilter customFilter = new CustomUsernamePasswordAuthenticationFilter();
        customFilter.setFilterProcessesUrl("/api/members/login"); // 로그인 URL 설정
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/members/login").permitAll() // 로그인 페이지 접근 허용
                            .anyRequest().permitAll() // 모든 요청을 허용

                )

                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1) // 동시 세션 수 제한
                        .maxSessionsPreventsLogin(false) // 새 로그인이 기존 세션을 무효화하지 않음
                        .sessionRegistry(sessionRegistry())
                )
              .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/members/login") // 로그인 처리 경로 설정
                        .successHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                        .failureHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .permitAll() // 로그인 페이지는 모든 사용자에게 허용
                )
                .httpBasic(Customizer.withDefaults())
                // .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .invalidateHttpSession(true) // 로그아웃 시 세션 무효화
                        .deleteCookies("JSESSIONID")
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .userDetailsService(customUserDetailsService);
        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } // 비밀번호 암호화에 사용할 BCryptPasswordEncoder 빈 등록

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    } // 세션 이벤트 처리를 위한 HttpSessionEventPublisher 빈 등록

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
    } // 동시 세션 관리를 위한 전략 설정

    @Bean
    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    } // 세션 레지스트리 빈 등록

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 자격 증명을 포함한 CORS 요청 허용
        configuration.setMaxAge(3600L); // CORS 구성 캐싱 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 구성 등록
        return source;
    }
}
