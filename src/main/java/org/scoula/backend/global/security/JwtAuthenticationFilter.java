package org.scoula.backend.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String path = request.getServletPath();

		// ⭐ JWT 검사 제외 경로 (SecurityConfig permitAll 과 동일하게!)
		if (
			path.startsWith("/auth/login") ||
				path.startsWith("/auth/password") ||
				path.startsWith("/member/register") ||
				path.startsWith("/member/check-email") ||
				path.startsWith("/member/family-info") ||
				path.startsWith("/kakao") ||
				path.startsWith("/callback") ||
				path.startsWith("/gallery/items") ||
				path.startsWith("/post/pet") ||
				path.startsWith("/home") ||
				path.startsWith("/swagger-ui") ||
				path.startsWith("/swagger-ui.html") ||
				path.startsWith("/v3/api-docs") ||
				path.startsWith("/files") ||
				path.startsWith("/uploads")
		) {
			filterChain.doFilter(request, response);
			return;
		}

		// ⭐ JWT 인증 체크
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			if (jwtTokenProvider.validateToken(token)) {
				String email = jwtTokenProvider.getEmail(token);

				UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
						new User(email, "", Collections.emptyList()),
						null,
						Collections.emptyList()
					);

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		filterChain.doFilter(request, response);
	}


}
