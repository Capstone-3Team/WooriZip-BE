package org.scoula.backend.domain.Auth.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.Auth.dto.LoginRequest;
import org.scoula.backend.domain.Auth.dto.LoginResponse;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.global.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final FamilyMemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final BCryptPasswordEncoder passwordEncoder;

	public LoginResponse login(LoginRequest request) {
		FamilyMember member = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String token = jwtTokenProvider.generateToken(member.getEmail());
		return new LoginResponse(token, "로그인 성공");
	}
}
