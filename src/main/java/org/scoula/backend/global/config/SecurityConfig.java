package org.scoula.backend.global.config;

import java.util.List;

import org.scoula.backend.global.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))   // CORS 활성화
			.csrf(csrf -> csrf.disable()) // Postman 테스트용 CSRF 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/member/register","/home", "/auth/login","/auth/password/**" ,"/v3/api-docs/**",
					"/swagger-ui/**",
					"/swagger-ui.html","/member/family-info","/member/check-email","/post/pet","/callback","/kakao/**").permitAll()
				// 질문, 영상 답변은 로그인 필요
				.requestMatchers("/question/**", "/video-answer/**","/post/**").authenticated()
				.anyRequest().authenticated() // 그 외 요청은 인증 필요
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

			.httpBasic(httpBasic -> httpBasic.disable()) // REST API는 기본 로그인 비활성화
			.formLogin(form -> form.disable()); // 폼 로그인 비활성화

		return http.build();
	}

	// CORS 설정
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173")); // ⭐ 프론트 개발 서버
		config.setAllowedMethods(List.of("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	// ✅ BCryptPasswordEncoder Bean 등록
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
