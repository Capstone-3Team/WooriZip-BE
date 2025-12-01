package org.scoula.backend.domain.archive.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.domain.archive.dto.PetGalleryItemResponse;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.scoula.backend.global.s3.S3Downloader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetPostService {

	private final FamilyMemberRepository familyMemberRepository;
	private final VideoAnswerRepository videoAnswerRepository;
	private final PostMapper postMapper;
	private final AIService aiService;
	private final S3Downloader s3Downloader;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	public List<PetGalleryItemResponse> getPetPosts(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Integer familyId = member.getFamilyId();

		// --- POST 조회 ---
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		List<PetGalleryItemResponse> postItems = new ArrayList<>();

		for (PostResponse post : posts) {

			// 게시글의 모든 media URL 가져오기
			List<String> mediaUrls = postMapper.findMediaByPostId(post.getId());

			for (String mediaUrl : mediaUrls) {

				// ✔ URL → key 변환
				String key = extractKeyFromUrl(mediaUrl);

				File tempFile;
				try {
					// ✔ 정확한 key로 다운로드
					tempFile = s3Downloader.downloadAsTemp(key);
				} catch (Exception e) {
					System.out.println("❌ S3 다운로드 실패: " + e.getMessage());
					continue;
				}

				// ✔ Flask AI 분석
				if (aiService.hasPet(tempFile.getAbsolutePath())) {

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

				tempFile.delete();
			}
		}

		// --- SHORTS 조회 ---
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

		// 최종 병합 & 정렬
		List<PetGalleryItemResponse> result = new ArrayList<>();
		result.addAll(postItems);
		result.addAll(shortsItems);

		result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		return result;
	}

	// 🔥 최종 버전 (모든 URL 처리 가능)
	private String extractKeyFromUrl(String url) {
		if (url == null) return null;

		// https://bucket.s3.region.amazonaws.com/folder/file
		int idx = url.indexOf(".amazonaws.com/");
		if (idx != -1) {
			return url.substring(idx + ".amazonaws.com/".length());
		}

		// s3://bucket/key
		if (url.startsWith("s3://")) {
			return url.substring(url.indexOf('/', 5) + 1);
		}

		// 이미 key일 경우
		return url;
	}
}
