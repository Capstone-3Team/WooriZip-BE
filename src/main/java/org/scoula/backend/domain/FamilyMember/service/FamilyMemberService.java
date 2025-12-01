package org.scoula.backend.domain.FamilyMember.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.global.s3.S3Uploader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {

	private final FamilyRepository familyRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final S3Uploader s3Uploader;

	/**
	 * YYYYMMDD 형태의 문자열을 LocalDate로 변환하는 메서드
	 */
	private LocalDate parseBirth(String birth) {
		if (birth == null || birth.length() != 8) {
			throw new IllegalArgumentException("생년월일은 YYYYMMDD 형식의 8자리여야 합니다.");
		}

		String formatted = birth.substring(0, 4) + "-" +
			birth.substring(4, 6) + "-" +
			birth.substring(6, 8);

		return LocalDate.parse(formatted);
	}

	@Transactional
	public String registerMember(MemberRegisterRequest request, MultipartFile profileImage) {

		// 1) S3 업로드 (파일이 있을 때만)
		String profileImageUrl = null;
		if (profileImage != null && !profileImage.isEmpty()) {
			profileImageUrl = s3Uploader.upload(profileImage, "profile-images");
		}

		Family family;

		// 1️⃣ 가족 코드 없으면 → 새 가족 생성
		if (request.getInviteCode() == null || request.getInviteCode().isEmpty()) {

			String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

			family = Family.builder()
				.name(request.getFamilyName())
				.inviteCode(inviteCode)
				.createdAt(java.time.LocalDateTime.now())
				.build();
			familyRepository.save(family);

			// 리더 생성
			FamilyMember leader = FamilyMember.builder()
				.familyId(family.getId())
				.email(request.getEmail())
				.nickname(request.getNickname())
				.birth(parseBirth(request.getBirth()))
				.phone(request.getPhone())
				.profileImage(profileImageUrl)   // ⭐ 여기가 중요
				.password(passwordEncoder.encode(request.getPassword()))
				.isLeader(true)
				.build();

			familyMemberRepository.save(leader);

			family.setLeaderMemberId(leader.getId());
			familyRepository.save(family);

			return inviteCode;

		} else {
			// 2️⃣ 기존 가족 참가
			Optional<Family> optionalFamily = familyRepository.findByInviteCode(request.getInviteCode());
			if (optionalFamily.isEmpty()) {
				throw new IllegalArgumentException("유효하지 않은 가족코드입니다.");
			}

			family = optionalFamily.get();

			FamilyMember newMember = FamilyMember.builder()
				.familyId(family.getId())
				.email(request.getEmail())
				.nickname(request.getNickname())
				.birth(parseBirth(request.getBirth()))
				.phone(request.getPhone())
				.profileImage(profileImageUrl)   // ⭐ 여기도 중요
				.password(passwordEncoder.encode(request.getPassword()))
				.isLeader(false)
				.build();

			familyMemberRepository.save(newMember);

			return family.getInviteCode();
		}
	}


}