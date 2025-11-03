package org.scoula.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // Postman 테스트용 CSRF 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/member/register","/api/auth/login").permitAll() // ✅ 회원가입은 허용
				.anyRequest().authenticated() // 그 외 요청은 인증 필요
			)
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
