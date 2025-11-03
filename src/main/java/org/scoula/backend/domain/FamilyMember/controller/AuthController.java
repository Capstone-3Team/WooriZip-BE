package org.scoula.backend.domain.FamilyMember.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.dto.LoginRequest;
import org.scoula.backend.domain.FamilyMember.dto.LoginResponse;
import org.scoula.backend.domain.FamilyMember.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
}
