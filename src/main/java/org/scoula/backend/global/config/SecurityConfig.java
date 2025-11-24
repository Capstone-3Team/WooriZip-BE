package org.scoula.backend.global.config;

import org.scoula.backend.global.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // Postman 테스트용 CSRF 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/member/register", "/auth/login","/auth/password/**" ,"/v3/api-docs/**",
					"/swagger-ui/**",
					"/swagger-ui.html").permitAll()
				// 질문, 영상 답변은 로그인 필요
				.requestMatchers("/questions/**", "/api/video-answers/**","/api/posts/**").authenticated()
				.anyRequest().authenticated() // 그 외 요청은 인증 필요
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

			.httpBasic(httpBasic -> httpBasic.disable()) // REST API는 기본 로그인 비활성화
			.formLogin(form -> form.disable()); // 폼 로그인 비활성화

		return http.build();
	}
	// ✅ BCryptPasswordEncoder Bean 등록
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
