package org.scoula.backend.domain.FamilyMember.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {

	private final FamilyRepository familyRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Transactional
	public String registerMember(MemberRegisterRequest request) {
		Family family;
		// 1️⃣ 가족코드 있는지 확인
		if (request.getInviteCode() == null || request.getInviteCode().isEmpty()) {
			// 가족코드 없음 → 새 가족 생성
			String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
			family = Family.builder()
				.name(request.getFamilyName())
				.inviteCode(inviteCode)
				.createdAt(java.time.LocalDateTime.now())
				.build();
			familyRepository.save(family);
			// 대표 가족 구성원 생성
			FamilyMember leader = FamilyMember.builder()
				.familyId(family.getId())
				.email(request.getEmail())
				.nickname(request.getNickname())
				.birth(LocalDate.parse(request.getBirth()))
				.phone(request.getPhone())
				.profileImage(request.getProfileImage())
				.password(passwordEncoder.encode(request.getPassword()))
				.isLeader(true)
				.build();
			familyMemberRepository.save(leader);
			// 리더 ID를 Family 테이블에 반영
			family.setLeaderMemberId(leader.getId());
			familyRepository.save(family);
			return inviteCode;

		} else {
			// 2️⃣ 가족코드 있음 → 기존 가족 참여
			Optional<Family> optionalFamily = familyRepository.findByInviteCode(request.getInviteCode());
			if (optionalFamily.isEmpty()) {
				throw new IllegalArgumentException("유효하지 않은 가족코드입니다.");
			}

			family = optionalFamily.get();

			FamilyMember newMember = FamilyMember.builder()
				.familyId(family.getId())
				.email(request.getEmail())
				.nickname(request.getNickname())
				.birth(LocalDate.parse(request.getBirth()))
				.phone(request.getPhone())
				.profileImage(request.getProfileImage())
				.password(passwordEncoder.encode(request.getPassword()))
				.isLeader(false)
				.build();

			familyMemberRepository.save(newMember);

			return family.getInviteCode();
		}
	}
}