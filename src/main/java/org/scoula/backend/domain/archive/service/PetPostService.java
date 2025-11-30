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
	private final PostMapper postMapper;    // ✔️ 이거 하나면 충분!
	private final AIService aiService;

	@Value("${file.upload.path}")
	private String uploadPath;

	public List<PetGalleryItemResponse> getPetPosts(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Integer familyId = member.getFamilyId();

		// 1) 가족 게시글 조회
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		List<PetGalleryItemResponse> postItems = new ArrayList<>();

		for (PostResponse post : posts) {

			// ✔️ 이 게시글의 모든 media 조회 (이미 PostMapper에 있음)
			List<String> mediaUrls = postMapper.findMediaByPostId(post.getId());

			for (String mediaUrl : mediaUrls) {
				String fullPath = Paths.get(uploadPath, mediaUrl).toString();

				// 강아지가 나온 사진만 모아보기 추가
				if (aiService.hasPet(fullPath)) {
					postItems.add(
						PetGalleryItemResponse.builder()
							.type("POST")
							.id(post.getId())
							.mediaUrl(mediaUrl)
							.description(post.getDescription())
							.writerNickname(post.getWriterNickname())
							.writerProfile(post.getWriterProfile())
							.createdAt(post.getCreatedAt().toString())
							.build()
					);
				}
			}
		}

		// 2) 숏츠 처리 (그대로)
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
			.toList();

		// 3) 합치기 + 최신순 정렬
		List<PetGalleryItemResponse> result = new ArrayList<>();
		result.addAll(postItems);
		result.addAll(shortsItems);

		result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		return result;
	}
}
