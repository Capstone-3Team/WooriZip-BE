package org.scoula.backend.domain.mypage.service;


import java.util.List;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.mypage.dto.ChangePasswordRequest;
import org.scoula.backend.domain.mypage.dto.FamilyNameUpdateRequest;
import org.scoula.backend.domain.mypage.dto.FamilyProfileResponse;
import org.scoula.backend.domain.mypage.dto.MyPageMainResponse;
import org.scoula.backend.domain.mypage.dto.MyPageProfileResponse;
import org.scoula.backend.domain.mypage.dto.UpdateFieldRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

	private final FamilyMemberRepository familyMemberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final FamilyRepository familyRepository;

	/** 이메일로 회원 객체 조회 (JWT 인증 기반) */
	private FamilyMember findMemberByEmail(String email) {
		return familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
	}

	/** 프로필 조회 */
	public MyPageProfileResponse getProfile(String email) {
		FamilyMember member = findMemberByEmail(email);

		return new MyPageProfileResponse(
			member.getProfileImage(),
			member.getNickname(),
			member.getEmail(),
			member.getBirth().toString(),
			member.getPhone()
		);
	}

	/** 별명 수정 */
	@Transactional
	public void updateNickname(String email, UpdateFieldRequest request) {
		FamilyMember member = findMemberByEmail(email);
		member.setNickname(request.getValue());
	}

	/** 이메일 수정 */
	@Transactional
	public void updateEmail(String email, UpdateFieldRequest request) {

		// 새 이메일 중복 체크
		if (familyMemberRepository.findByEmail(request.getValue()).isPresent()) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		FamilyMember member = findMemberByEmail(email);
		member.setEmail(request.getValue());
	}

	/** 휴대폰 번호 수정 */
	@Transactional
	public void updatePhone(String email, UpdateFieldRequest request) {
		FamilyMember member = findMemberByEmail(email);
		member.setPhone(request.getValue());
	}

	/** 프로필 이미지 변경 */
	@Transactional
	public void updateProfileImage(String email, String profileImage) {
		FamilyMember member = findMemberByEmail(email);
		member.setProfileImage(profileImage);
	}


	/** 비밀번호 변경 (로그인 후) */
	@Transactional
	public void changePassword(String email, ChangePasswordRequest request) {

		FamilyMember member = findMemberByEmail(email);

		// 기존 비밀번호 확인
		if (!passwordEncoder.matches(request.getOldPassword(), member.getPassword())) {
			throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
		}

		// 새 비밀번호 저장
		member.setPassword(passwordEncoder.encode(request.getNewPassword()));
	}

	// // ===========================
	// // 가족 탈퇴
	// // ===========================
	// public void leaveFamily(String email) {
	//
	// 	FamilyMember member = familyMemberRepository.findByEmail(email)
	// 		.orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));
	//
	// 	// 가족 미가입
	// 	if (member.getFamilyId() == null) {
	// 		throw new IllegalArgumentException("가족에 가입되어 있지 않습니다.");
	// 	}
	//
	// 	Family family = familyRepository.findById(member.getFamilyId())
	// 		.orElseThrow(() -> new IllegalArgumentException("가족 정보를 찾을 수 없습니다."));
	//
	// 	// 대표면 탈퇴 불가
	// 	if (family.getLeaderMemberId().equals(member.getId())) {
	// 		throw new IllegalArgumentException("가족 대표는 탈퇴할 수 없습니다. 가족 해체 기능을 사용하세요.");
	// 	}
	//
	// 	// 가족 탈퇴 = familyId null 처리
	// 	member.setFamilyId(null);
	// 	familyMemberRepository.save(member);
	// }
	//
	// // ===========================
	// // 회원 탈퇴
	// // ===========================
	// public void withdraw(String email) {
	//
	// 	FamilyMember member = familyMemberRepository.findByEmail(email)
	// 		.orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));
	//
	// 	// 가족 대표는 탈퇴 불가
	// 	if (member.getFamilyId() != null) {
	// 		Family family = familyRepository.findById(member.getFamilyId())
	// 			.orElseThrow(() -> new IllegalArgumentException("가족 정보를 찾을 수 없습니다."));
	//
	// 		if (family.getLeaderMemberId().equals(member.getId())) {
	// 			throw new IllegalArgumentException("가족 대표는 회원 탈퇴가 불가능합니다. 가족 해체 기능을 사용하세요.");
	// 		}
	// 	}
	//
	// 	// 회원 삭제
	// 	familyMemberRepository.delete(member);
	// }

	public MyPageMainResponse getMainPage(String email) {

		FamilyMember member = findMemberByEmail(email);

		String familyName = null;
		if (member.getFamilyId() != null) {
			Family family = familyRepository.findById(member.getFamilyId())
				.orElse(null);
			if (family != null) {
				familyName = family.getName();
			}
		}

		return new MyPageMainResponse(
			member.getProfileImage(),
			member.getNickname(),
			familyName
		);
	}

	public FamilyProfileResponse getFamilyProfile(String email) {

		FamilyMember member = findMemberByEmail(email);

		// 가족 미가입 처리
		if (member.getFamilyId() == null) {
			throw new IllegalArgumentException("가족에 가입되어 있지 않습니다.");
		}

		Family family = familyRepository.findById(member.getFamilyId())
			.orElseThrow(() -> new IllegalArgumentException("가족 정보를 찾을 수 없습니다."));

		// 가족 대표 찾기
		FamilyMember leader = familyMemberRepository.findById(family.getLeaderMemberId())
			.orElseThrow(() -> new IllegalArgumentException("가족 대표 정보를 찾을 수 없습니다."));

		// 가족 구성원 찾기
		List<FamilyProfileResponse.MemberInfo> members = familyMemberRepository.findByFamilyId(family.getId())
			.stream()
			.filter(m -> !m.getId().equals(leader.getId())) // 대표 제외
			.map(m -> new FamilyProfileResponse.MemberInfo(
				m.getNickname(),
				m.getProfileImage()
			))
			.toList();

		// Response 생성
		return new FamilyProfileResponse(
			family.getName(),
			family.getInviteCode(),
			new FamilyProfileResponse.MemberInfo(
				leader.getNickname(),
				leader.getProfileImage()
			),
			members
		);
	}

	/** 가족 이름 수정 (가족 대표만 가능) */
	@Transactional
	public void updateFamilyName(String email, FamilyNameUpdateRequest request) {

		FamilyMember member = findMemberByEmail(email);

		// 가족 미가입 확인
		if (member.getFamilyId() == null) {
			throw new IllegalArgumentException("가족에 가입되어 있지 않습니다.");
		}

		Family family = familyRepository.findById(member.getFamilyId())
			.orElseThrow(() -> new IllegalArgumentException("가족 정보를 찾을 수 없습니다."));

		// 대표만 수정 가능
		if (!family.getLeaderMemberId().equals(member.getId())) {
			throw new IllegalArgumentException("가족 대표만 가족 이름을 수정할 수 있습니다.");
		}

		// 이름 변경
		family.setName(request.getFamilyName());
	}






}
