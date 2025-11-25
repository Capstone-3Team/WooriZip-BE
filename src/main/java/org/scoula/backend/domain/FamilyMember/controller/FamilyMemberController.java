package org.scoula.backend.domain.FamilyMember.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "Family Member", description = "회원가입 API")
public class FamilyMemberController {

	private final FamilyMemberService familyMemberService;

	@PostMapping("/register")
	@Operation(
		summary = "회원가입",
		description = "가족 구성원의 회원가입을 처리하고 가족 초대 코드를 반환합니다."
	)
	public ResponseEntity<?> register(@RequestBody MemberRegisterRequest request) {
		String inviteCode = familyMemberService.registerMember(request);
		return ResponseEntity.ok("회원가입 성공! 가족 코드: " + inviteCode);
	}
}
