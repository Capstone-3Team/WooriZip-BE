package org.scoula.backend.domain.Auth.controller;

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
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
	/** 이메일로 인증번호 발송 */
	@PostMapping("/password/email")
	public ResponseEntity<String> sendVerifyCode(@RequestBody EmailRequest request) {
		authService.sendVerificationCode(request.getEmail());
		return ResponseEntity.ok("인증번호가 발송되었습니다.");
	}

	/** 인증번호 확인 */
	@PostMapping("/password/verify")
	public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeRequest request) {
		authService.verifyCode(request);
		return ResponseEntity.ok("인증 완료되었습니다.");
	}

	/** 비밀번호 변경 */
	@PostMapping("/password/change")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
		authService.changePassword(request);
		return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	}
}