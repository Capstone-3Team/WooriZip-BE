package org.scoula.backend.domain.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.mypage.dto.ChangePasswordRequest;
import org.scoula.backend.domain.mypage.dto.FamilyNameUpdateRequest;
import org.scoula.backend.domain.mypage.dto.UpdateFieldRequest;
import org.scoula.backend.domain.mypage.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Tag(name = "My Page", description = "마이페이지 API")
public class MyPageController {

	private final MyPageService myPageService;

	/** JWT에서 email 가져오기 */
	private String getEmailFromToken() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/** 프로필 조회 */

	@GetMapping("/profile")
	@Operation(
		summary = "프로필 조회",
		description = "현재 로그인한 사용자의 프로필 정보를 반환합니다."
	)
	public ResponseEntity<?> getProfile() {
		String email = getEmailFromToken();
		return ResponseEntity.ok(myPageService.getProfile(email));
	}

	/** 별명 수정 */
	@PatchMapping("/nickname")
	@Operation(
		summary = "별명 수정",
		description = "현재 사용자의 별명(닉네임)을 수정합니다."
	)
	public ResponseEntity<?> updateNickname(@RequestBody UpdateFieldRequest request) {
		String email = getEmailFromToken();
		myPageService.updateNickname(email, request);
		return ResponseEntity.ok("별명 수정 성공");
	}

	/** 이메일 수정 */
	@PatchMapping("/email")
	@Operation(
		summary = "이메일 수정",
		description = "현재 사용자의 이메일 주소를 수정합니다."
	)
	public ResponseEntity<?> updateEmail(@RequestBody UpdateFieldRequest request) {
		String email = getEmailFromToken();
		myPageService.updateEmail(email, request);
		return ResponseEntity.ok("이메일 수정 성공");
	}

	/** 휴대폰 번호 수정 */
	@PatchMapping("/phone")
	@Operation(
		summary = "휴대폰 번호 수정",
		description = "현재 사용자의 휴대폰 번호를 수정합니다."
	)

	public ResponseEntity<?> updatePhone(@RequestBody UpdateFieldRequest request) {
		String email = getEmailFromToken();
		myPageService.updatePhone(email, request);
		return ResponseEntity.ok("휴대폰 번호 수정 성공");
	}

	@PatchMapping("/profile-image")
	@Operation(
		summary = "프로필 이미지 변경",
		description = "현재 사용자의 프로필 이미지를 변경합니다."
	)
	public ResponseEntity<?> updateProfileImage(@RequestParam("image") String profileImage) {
		String email = getEmailFromToken();
		myPageService.updateProfileImage(email, profileImage);
		return ResponseEntity.ok("프로필 이미지 변경 성공");
	}


	/** 비밀번호 변경 */
	@PatchMapping("/password")
	@Operation(
		summary = "비밀번호 변경",
		description = "기존 비밀번호 확인 후 새 비밀번호로 변경합니다."
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "비밀번호 변경 요청",
		required = true,
		content = @io.swagger.v3.oas.annotations.media.Content(
			schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChangePasswordRequest.class)
		)
	)
	public ResponseEntity<?> changePassword(
		@RequestBody ChangePasswordRequest request
	) {
		String email = getEmailFromToken();
		myPageService.changePassword(email, request);
		return ResponseEntity.ok("비밀번호 변경 성공");
	}


	// ===========================
	// 로그아웃
	// ===========================
	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "JWT 기반 인증에서는 서버 로그아웃이 필요하지 않으며, 클라이언트가 토큰을 삭제하면 로그아웃됩니다.")
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok("로그아웃 성공 (클라이언트에서 토큰 삭제 필요)");
	}

	// // ===========================
	// // 가족 탈퇴
	// // ===========================
	// @DeleteMapping("/family")
	// @Operation(summary = "가족 탈퇴", description = "현재 사용자를 가족 그룹에서 제거합니다. 가족 대표는 탈퇴할 수 없습니다.")
	// public ResponseEntity<?> leaveFamily() {
	// 	String email = getEmailFromToken();
	// 	myPageService.leaveFamily(email);
	// 	return ResponseEntity.ok("가족 탈퇴 성공");
	// }
	//
	// // ===========================
	// // 회원 탈퇴
	// // ===========================
	// @DeleteMapping("/withdraw")
	// @Operation(summary = "회원 탈퇴", description = "현재 사용자의 계정을 삭제합니다. 가족 대표는 회원 탈퇴가 불가능합니다.")
	// public ResponseEntity<?> withdraw() {
	// 	String email = getEmailFromToken();
	// 	myPageService.withdraw(email);
	// 	return ResponseEntity.ok("회원 탈퇴 성공");
	// }

	@GetMapping("/main")
	@Operation(
		summary = "마이페이지 메인 조회",
		description = "사용자의 별명, 프로필 이미지, 가족명을 조회합니다."
	)
	public ResponseEntity<?> getMainPage() {
		String email = getEmailFromToken();
		return ResponseEntity.ok(myPageService.getMainPage(email));
	}

	@GetMapping("/family-profile")
	@Operation(
		summary = "가족 프로필 조회",
		description = "가족 초대코드, 가족 대표, 구성원 목록을 조회합니다."
	)
	public ResponseEntity<?> getFamilyProfile() {
		String email = getEmailFromToken();
		return ResponseEntity.ok(myPageService.getFamilyProfile(email));
	}

	@PatchMapping("/family/name")
	@Operation(
		summary = "가족 이름 수정",
		description = "가족 이름(별명)을 수정할 수 있습니다."
	)
	public ResponseEntity<?> updateFamilyName(@RequestBody FamilyNameUpdateRequest request) {
		String email = getEmailFromToken();
		myPageService.updateFamilyName(email, request);
		return ResponseEntity.ok("가족 이름 수정 성공");
	}

	@GetMapping("/family/name/info")
	@Operation(
		summary = "가족 이름 수정 페이지 정보 조회",
		description = "현재 가족 이름과 마지막 수정자를 조회합니다."
	)
	public ResponseEntity<?> getFamilyNameEditInfo() {
		String email = getEmailFromToken();
		return ResponseEntity.ok(myPageService.getFamilyNameEditPageInfo(email));
	}




}
