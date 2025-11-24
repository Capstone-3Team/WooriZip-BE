package org.scoula.backend.domain.Auth.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.Auth.dto.LoginRequest;
import org.scoula.backend.domain.Auth.dto.LoginResponse;
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
}
