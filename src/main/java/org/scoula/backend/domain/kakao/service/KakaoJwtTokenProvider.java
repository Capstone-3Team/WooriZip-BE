package org.scoula.backend.domain.kakao.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class KakaoJwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	// 1시간
	private final long EXPIRATION = 1000L * 60 * 60;

	public String generateToken(Long userId) {
		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}
}
