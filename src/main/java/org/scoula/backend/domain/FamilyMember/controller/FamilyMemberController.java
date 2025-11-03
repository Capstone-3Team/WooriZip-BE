package org.scoula.backend.domain.FamilyMember.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class FamilyMemberController {

	private final FamilyMemberService familyMemberService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody MemberRegisterRequest request) {
		String inviteCode = familyMemberService.registerMember(request);
		return ResponseEntity.ok("회원가입 성공! 가족 코드: " + inviteCode);
	}
}
