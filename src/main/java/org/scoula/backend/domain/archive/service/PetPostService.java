package org.scoula.backend.domain.archive.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.domain.archive.dto.PetGalleryItemResponse;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetPostService {

	private final FamilyMemberRepository familyMemberRepository;
	private final VideoAnswerRepository videoAnswerRepository;
	private final PostMapper postMapper;
	private final AIService aiService;

	@Value("${file.upload.path}")
	private String uploadPath;

	public List<PetGalleryItemResponse> getPetPosts(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Integer familyId = member.getFamilyId();

		// 🔹 1) Post 중 반려동물 포함된 것
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		List<PetGalleryItemResponse> postItems = posts.stream()
			.filter(post -> {
				if (post.getMediaUrl() == null) return false;
				String fullPath = Paths.get(uploadPath, post.getMediaUrl()).toString();
				return aiService.hasPet(fullPath);
			})
			.map(post -> PetGalleryItemResponse.builder()
				.type("POST")
				.id(post.getId())
				.mediaUrl(post.getMediaUrl())
				.description(post.getDescription())
				.writerNickname(post.getWriterNickname())
				.writerProfile(post.getWriterProfile())
				.createdAt(post.getCreatedAt().toString())
				.build()
			)
			.collect(Collectors.toList());

		// 🔹 2) 반려동물 숏츠(DONE 상태만)
		List<VideoAnswer> shorts = videoAnswerRepository
			.findByFamilyIdAndShortsStatus(familyId.longValue(), "DONE");

		List<PetGalleryItemResponse> shortsItems = shorts.stream()
			.map(s -> PetGalleryItemResponse.builder()
				.type("SHORTS")
				.id(s.getId())
				.mediaUrl(s.getVideoUrl())
				.thumbnailUrl(s.getThumbnailUrl())
				.title(s.getTitle())
				.summary(s.getSummary())
				.shortsUrl(s.getShortsUrl())
				.createdAt(s.getCreatedAt().toString())
				.build()
			)
			.collect(Collectors.toList());

		// 🔹 3) 합치기
		List<PetGalleryItemResponse> result = new ArrayList<>();
		result.addAll(postItems);
		result.addAll(shortsItems);

		// 🔹 4) 최신순 정렬
		result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		return result;
	}

}
