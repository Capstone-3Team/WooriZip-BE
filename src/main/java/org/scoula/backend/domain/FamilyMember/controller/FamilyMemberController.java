package org.scoula.backend.domain.FamilyMember.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.dto.FamilyInfoResponse;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.FamilyMember.service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "Family Member", description = "회원가입 API")
public class FamilyMemberController {

	private final FamilyMemberService familyMemberService;
	private final FamilyRepository familyRepository;
	private final FamilyMemberRepository familyMemberRepository;

	@PostMapping("/register")
	@Operation(
		summary = "회원가입",
		description = "가족 구성원의 회원가입을 처리하고 가족 초대 코드를 반환합니다."
	)
	public ResponseEntity<?> register(@RequestBody MemberRegisterRequest request) {
		String inviteCode = familyMemberService.registerMember(request);
		return ResponseEntity.ok("회원가입 성공! 가족 코드: " + inviteCode);
	}

	// 가족 코드로 가족 정보 조회
	@GetMapping("/family-info")
	@Operation(
		summary = "가족코드로 가족 정보 조회",
		description = "가족코드를 기반으로 가족 별명과 대표 정보를 반환합니다."
	)
	public ResponseEntity<?> getFamilyInfo(@RequestParam String inviteCode) {

		// 1. 가족코드 검증
		Family family = familyRepository.findByInviteCode(inviteCode)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 가족코드입니다."));

		// 2. 대표 멤버 찾기
		FamilyMember leader = familyMemberRepository.findById(family.getLeaderMemberId())
			.orElseThrow(() -> new IllegalArgumentException("가족 대표 정보를 찾을 수 없습니다."));

		// 3. 응답 DTO 생성
		FamilyInfoResponse response = new FamilyInfoResponse(
			family.getName(),              // 가족별명
			leader.getId(),                // 대표 ID
			leader.getNickname(),          // 대표 닉네임
			leader.getProfileImage()       // 대표 프로필
		);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/check-email")
	@Operation(
		summary = "이메일 중복확인",
		description = "입력된 이메일이 이미 회원가입되어 있는지 확인합니다."
	)
	public ResponseEntity<?> checkEmail(@RequestParam String email) {
		boolean isDuplicate = familyMemberRepository.findByEmail(email).isPresent();

		if (isDuplicate) {
			return ResponseEntity.ok(false); // false = 사용 불가
		} else {
			return ResponseEntity.ok(true);  // true = 사용 가능
		}
	}

}
