package org.scoula.backend.domain.FamilyMember.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.dto.MemberRegisterRequest;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {

	private final FamilyRepository familyRepository;
	private final FamilyMemberRepository familyMemberRepository;

	@Transactional
	public void register(MemberRegisterRequest request) {
		Family family;

		// 초대 코드가 없는 경우: 새 가족 생성
		if (request.getInviteCode() == null || request.getInviteCode().isEmpty()) {
			family = Family.builder()
				.name(request.getFamilyName())
				.inviteCode(UUID.randomUUID().toString())
				.build();
			familyRepository.save(family);
		} else {
			family = familyRepository.findByInviteCode(request.getInviteCode())
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 가족 코드입니다."));
		}

		// 가족 구성원 생성
		FamilyMember member = FamilyMember.builder()
			.familyId(family.getId())
			.email(request.getEmail())
			.nickname(request.getNickname())
			.gender(FamilyMember.Gender.valueOf(request.getGender().toUpperCase()))
			.birth(LocalDate.parse(request.getBirth()))
			.phone(request.getPhone())
			.profileImage(request.getProfileImage())
			.isLeader(false)
			.build();

		familyMemberRepository.save(member);

		// 새 가족일 경우, 리더 설정
		if (request.getInviteCode() == null || request.getInviteCode().isEmpty()) {
			family.setLeaderMemberId(member.getId());
			member.setIsLeader(true);
		}

		familyRepository.save(family);
		familyMemberRepository.save(member);
	}
}
