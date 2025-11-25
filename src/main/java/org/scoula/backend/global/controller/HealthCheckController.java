package org.scoula.backend.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Tag(name = "Checklist", description = "서버 health check")
public class HealthCheckController {

	@Operation(summary = "헬스 체크 ", description = "서버 정상 동작 여부를 확인합니다. 인증 불필요.")
	@GetMapping("")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("OK");
	}
}