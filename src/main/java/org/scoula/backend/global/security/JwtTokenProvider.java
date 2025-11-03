package org.scoula.backend.global.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Component
public class JwtTokenProvider {

	// ✅ 1. 안전한 키 생성 (Base64 아님)
	private final Key secretKey = Keys.hmacShaKeyFor("my-secret-key-my-secret-key-my-secret-key".getBytes());

	// ✅ 2. 토큰 유효 시간 (예: 1시간)
	private final long validityInMs = 1000L * 60 * 60;

	// ✅ 3. 토큰 생성
	public String generateToken(String email) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMs);

		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(secretKey, SignatureAlgorithm.HS256) // ✅ secretKey 객체 사용
			.compact();
	}

	// ✅ 4. 이메일(subject) 추출
	public String getEmail(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}
}
