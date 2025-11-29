package org.scoula.backend.domain.archive.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetPostService {

	private final FamilyMemberRepository familyMemberRepository;
	private final PostMapper postMapper;
	private final AIService aiService;

	@Value("${file.upload.path}")
	private String uploadPath;

	public List<PostResponse> getPetPosts(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// ✔ PostResponse 로 수정됨
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(member.getFamilyId());

		return posts.stream()
			.filter(post -> {

				if (post.getMediaUrl() == null) return false;

				// 안전한 경로 결합
				String fullPath = Paths.get(uploadPath, post.getMediaUrl()).toString();

				return aiService.hasPet(fullPath);
			})
			.collect(Collectors.toList());
	}
}
