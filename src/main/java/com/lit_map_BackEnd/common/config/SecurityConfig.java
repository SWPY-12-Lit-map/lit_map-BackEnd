package com.lit_map_BackEnd.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import static org.springframework.security.config.annotation.web.builders.HttpSecurity.*;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;
/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .antMatchers("/api/version/**").hasRole("ADMIN") // /api/versions/** 엔드포인트는 ADMIN 권한이 필요합니다.
                .anyRequest().authenticated()
                .and()
                .httpBasic(); // Basic Authentication을 사용합니다. (테스트 목적)



        return http.build();
    }
*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig)->
                        csrfConfig.disable()
                )
                .headers((headerConfig)->
                        headerConfig.frameOptions((frameOptionsConfig ->
                                frameOptionsConfig.sameOrigin())
                        )
                )
            /*    .cors(withDefaults())
                .sessionManagement((sessionManagementConfig)->
                        sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )*/
                .formLogin((formLoginConfig)->
                        formLoginConfig.disable()
                )
             /*   .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(new UserAuthenticationEntryPoint()).accessDeniedHandler(new UserAccessDeniedHandler())
                )*/
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.PUT, "/api/version/**").hasRole("ADMIN")
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()

                );

        return http.build();

    }

/*
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 패스워드 인코더로서 NoOpPasswordEncoder를 사용 (테스트 목적, 실제로는 사용하지 않는 것이 좋음)
        return NoOpPasswordEncoder.getInstance();
        //  return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // 실제 UserDetailsService를 사용하여 인증을 구성하는 예시
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

 */
}
