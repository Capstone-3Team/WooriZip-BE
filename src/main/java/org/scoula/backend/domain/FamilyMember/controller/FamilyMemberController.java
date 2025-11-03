package org.scoula.backend.domain.FamilyMember.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class FamilyMemberController {

	private final FamilyMemberService familyMemberService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody MemberRegisterRequest request) {
		familyMemberService.register(request);
		return ResponseEntity.ok("회원가입 완료!");
	}
}

