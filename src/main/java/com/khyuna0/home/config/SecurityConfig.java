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

	@Bean // 깃허브 브랜치 추가 테스트
	   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	       http
	           .csrf(csrf -> csrf.disable())  //새 방식 (람다 DSL)
	           .cors(Customizer.withDefaults())
	           .authorizeHttpRequests(auth -> auth
	        		   // 리액트 라우터 설정 경로 허용
	                 .requestMatchers(
	                       "/", 
	                       "/index.html", 
	                       "/login", 
	                       "/signup", 
	                       "/board/**", 
	                       "/static/**")
	                 .permitAll()                   
	                   // 읽기 API는 로그인 없이 허용
	                   .requestMatchers(
	                         "/api/board", 
	                         "/api/board/**", 
	                         "/api/comments", 
	                         "/api/comments/**")
	                   .permitAll()
	                   
	                   // 쓰기/수정/삭제 API는 인증 필요
	                   .requestMatchers(
	                         "/api/board/write", 
	                         "/api/board/update/**", 
	                         "/api/board/delete/**")
	                   .authenticated()
	                   .requestMatchers(
	                         "/api/comments/write", 
	                         "/api/comments/delete/**")
	                   .authenticated()           
	               .anyRequest().authenticated()
	           )         
	           .formLogin(login -> login    
	              .loginPage("/login").permitAll()   
	               .loginProcessingUrl("/api/auth/login")
	               .usernameParameter("username")
	               .passwordParameter("password")
	               .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
	               .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
	           )
	           .logout(logout -> logout
	               .logoutUrl("/api/auth/logout")
	               .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
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