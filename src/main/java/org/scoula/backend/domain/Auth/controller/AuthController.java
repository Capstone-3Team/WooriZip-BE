package org.scoula.backend.domain.Auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Auth.dto.ChangePasswordRequest;
import org.scoula.backend.domain.Auth.dto.EmailRequest;
import org.scoula.backend.domain.Auth.dto.LoginRequest;
import org.scoula.backend.domain.Auth.dto.LoginResponse;
import org.scoula.backend.domain.Auth.dto.VerifyCodeRequest;
import org.scoula.backend.domain.Auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인 & 비밀번호 재설정(이메일 인증) API")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(
		summary = "로그인",
		description = "이메일과 비밀번호로 로그인하며 JWT 토큰을 반환합니다."
	)
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
	/** 이메일로 인증번호 발송 */
	@PostMapping("/password/email")
	@Operation(
		summary = "이메일 인증번호 발송",
		description = "회원 이메일로 3분간 유효한 인증번호를 발송합니다."
	)
	public ResponseEntity<String> sendVerifyCode(@RequestBody EmailRequest request) {
		authService.sendVerificationCode(request.getEmail());
		return ResponseEntity.ok("인증번호가 발송되었습니다.");
	}

	/** 인증번호 확인 */
	@PostMapping("/password/verify")
	@Operation(
		summary = "이메일 인증번호 확인",
		description = "사용자가 입력한 인증번호가 유효한지 검증합니다."
	)
	public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeRequest request) {
		authService.verifyCode(request);
		return ResponseEntity.ok("인증 완료되었습니다.");
	}

	/** 비밀번호 변경 */
	@PostMapping("/password/change")
	@Operation(
		summary = "비밀번호 변경",
		description = "인증 완료된 이메일의 비밀번호를 새 비밀번호로 변경합니다."
	)
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
		authService.changePassword(request);
		return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	}
}