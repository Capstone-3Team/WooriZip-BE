package org.scoula.backend.domain.archive.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.domain.archive.domain.PetMedia;
import org.scoula.backend.domain.archive.mapper.PetMediaMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetArchiveService {

	private final PetMediaMapper petMediaMapper;
	private final FamilyMemberRepository familyMemberRepository;
	private final VideoAnswerRepository videoAnswerRepository;

	public Map<String, Object> getPetArchive(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 🐶 일상피드에서 감지된 반려동물 이미지
		List<PetMedia> images = petMediaMapper.findPetMediaByFamilyId(member.getFamilyId());

		// 🎬 숏츠 영상 (VideoAnswer 테이블)
		List<VideoAnswer> shorts = videoAnswerRepository.findPetShortsByFamilyId(member.getFamilyId());

		Map<String, Object> result = new HashMap<>();
		result.put("images", images);
		result.put("shorts", shorts);
		return result;
	}
}
