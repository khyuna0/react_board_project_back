package com.khyuna0.home.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (API 테스트용)
            .csrf(csrf -> csrf.disable()) // csrf 인증 비활성화 -> 리액트, vue 같은 프론트 + 백엔드 구조 -> 불필요
            // CORS 기본 허용
            .cors(Customizer.withDefaults()) // cors 활성화
            // URL 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/board", "/api/board/**").permitAll()
                .anyRequest().authenticated()
            )

            // 로그인 설정
            .formLogin(login -> login // 아이디와 비밀번호 확인은 여기서!! -> 로그인 확인되면 세션까지 생성해줌
        		.loginProcessingUrl("/api/auth/login") // 로그인 요청 url 
                .usernameParameter("username")
                .passwordParameter("password")
                
                // api로 로그인 요청시 추가 사항            
                // 로그인이 성공 시 -> ok (200 - 상태코드)
                .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                // 실패 시 -> fail -> 401 (200 외)
                .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout") // 로그아웃 요청 url
                .logoutSuccessHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_OK)) 
                // 로그아웃 성공 시 200 응답 
            );

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    // 프론트엔드 리액트에서 요청하는 주소 허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // React 개발 서버
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키, 세션 허용 시 필요

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
}
