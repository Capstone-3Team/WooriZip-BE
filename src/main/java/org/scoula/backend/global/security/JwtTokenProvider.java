package org.scoula.backend.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	// ✅ 비밀 키 (안전한 길이 확보)
	private final Key secretKey = Keys.hmacShaKeyFor("my-secret-key-my-secret-key-my-secret-key".getBytes());

	// ✅ 토큰 유효 시간 (1시간)
	private final long validityInMs = 1000L * 60 * 60;

	// ✅ 토큰 생성
	public String generateToken(String email) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMs);

		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	// ✅ 이메일(subject) 추출
	public String getEmail(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	// ✅ 토큰 유효성 검증 추가
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			System.out.println("❌ Invalid JWT Token: " + e.getMessage());
			return false;
		}
	}
}
