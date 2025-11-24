package org.scoula.backend.domain.Auth.service;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Auth.dto.ChangePasswordRequest;
import org.scoula.backend.domain.Auth.dto.VerifyCodeRequest;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.Auth.dto.LoginRequest;
import org.scoula.backend.domain.Auth.dto.LoginResponse;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.global.email.EmailService;
import org.scoula.backend.global.security.JwtTokenProvider;
import org.scoula.backend.global.verify.VerificationCodeStore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final FamilyMemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final BCryptPasswordEncoder passwordEncoder;
	private final VerificationCodeStore codeStore;
	private final EmailService emailService;
	public LoginResponse login(LoginRequest request) {
		FamilyMember member = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String token = jwtTokenProvider.generateToken(member.getEmail());
		return new LoginResponse(token, "로그인 성공");
	}

	/** STEP 1: 이메일로 인증번호 전송 */
	public void sendVerificationCode(String email) {
		if (!memberRepository.findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
		}

		String code = codeStore.generateCode(email);  // 6자리 코드 생성 및 저장
		emailService.sendEmail(email, "비밀번호 변경 인증번호", "인증번호: " + code);
	}

	/** STEP 2: 인증번호 확인 */
	public void verifyCode(VerifyCodeRequest request) {
		boolean verified = codeStore.verifyCode(request.getEmail(), request.getCode());
		if (!verified) {
			throw new IllegalArgumentException("인증번호가 올바르지 않습니다.");
		}
	}

	/** STEP 3: 비밀번호 변경 */
	public void changePassword(ChangePasswordRequest request) {
		String email = request.getEmail();

		if (!codeStore.isVerified(email)) {
			throw new IllegalArgumentException("인증되지 않은 이메일입니다.");
		}

		FamilyMember member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		member.setPassword(passwordEncoder.encode(request.getNewPassword()));
		memberRepository.save(member);

		// 변경 완료 후 인증 기록 삭제
		codeStore.clear(email);
	}
}