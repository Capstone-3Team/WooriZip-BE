package org.scoula.backend.domain.archive.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.domain.archive.domain.PetMedia;
import org.scoula.backend.domain.archive.dto.PetGalleryItemResponse;
import org.scoula.backend.domain.archive.mapper.PetMediaMapper;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.scoula.backend.global.s3.S3Downloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetPostService {

	private final FamilyMemberRepository familyMemberRepository;
	private final VideoAnswerRepository videoAnswerRepository;
	private final PostMapper postMapper;
	private final PetMediaMapper petMediaMapper;
	private final AIService aiService;
	private final S3Downloader s3Downloader;

	public List<PetGalleryItemResponse> getPetPosts(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Long familyMemberId = member.getId();
		Integer familyId = member.getFamilyId();

		List<PetGalleryItemResponse> result = new ArrayList<>();

		/* ---------------------------------------------
		 * 1) POST (이미지·영상) — 캐싱 + 증분 업데이트
		 * --------------------------------------------- */
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		for (PostResponse post : posts) {

			List<String> mediaUrls = postMapper.findMediaByPostId(post.getId());

			for (String url : mediaUrls) {

				// 캐시 확인
				PetMedia cached = petMediaMapper.findByMediaUrl(url);

				// 신규 미디어라면 AI 분석 실행
				if (cached == null) {

					boolean isPet = analyzeMedia(url); // 실패 시 false

					PetMedia newRecord = new PetMedia();
					newRecord.setMediaUrl(url);
					newRecord.setPostId(post.getId());
					newRecord.setFamilyMemberId(post.getFamilyMemberId());
					newRecord.setIsPet(isPet);

					petMediaMapper.insertPetMedia(newRecord);
					cached = newRecord;
				}

				// is_pet null 방지
				if (!Boolean.TRUE.equals(cached.getIsPet())) {
					continue;  // null 포함 false는 skip
				}

				// 반려동물 포함된 미디어만 추가
				FamilyMember writer = familyMemberRepository.findById(post.getFamilyMemberId())
					.orElseThrow(() -> new IllegalArgumentException("업로더 정보를 찾을 수 없습니다."));

				result.add(
					PetGalleryItemResponse.builder()
						.type("POST")
						.id(post.getId())
						.mediaUrl(url)
						.description(post.getDescription())
						.writerNickname(writer.getNickname())
						.writerProfile(writer.getProfileImage())
						.createdAt(post.getCreatedAt().toString())
						.build()
				);
			}
		}

		/* ---------------------------------------------
		 * 2) SHORTS — 숏츠는 이미 DONE 된 것만 사용
		 * --------------------------------------------- */
		List<VideoAnswer> shorts =
			videoAnswerRepository.findByFamilyIdAndShortsStatus(familyId.longValue(), "DONE");

		for (VideoAnswer s : shorts) {
			result.add(
				PetGalleryItemResponse.builder()
					.type("SHORTS")
					.id(s.getId())
					.mediaUrl(s.getVideoUrl())
					.thumbnailUrl(s.getThumbnailUrl())
					.title(s.getTitle())
					.summary(s.getSummary())
					.shortsUrl(s.getShortsUrl())
					.createdAt(s.getCreatedAt().toString())
					.build()
			);
		}

		// 최신순 정렬
		result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		return result;
	}


	/* ----------------------------------------------------
	 * 🔥 URL → S3 Key 추출 (정확 버전)
	 * ---------------------------------------------------- */
	private String extractKeyFromUrl(String url) {
		if (url == null) return null;

		String marker = ".amazonaws.com/";
		int idx = url.indexOf(marker);

		if (idx != -1) {
			return url.substring(idx + marker.length());
		}

		if (url.startsWith("s3://")) {
			return url.substring(url.indexOf('/', 5) + 1);
		}

		return url; // 이미 key
	}

	/* ----------------------------------------------------
	 * 🔥 S3 다운로드 → AI 분석 → 실패시 false 반환
	 * ---------------------------------------------------- */
	private boolean analyzeMedia(String mediaUrl) {
		try {
			String key = extractKeyFromUrl(mediaUrl);
			File file = s3Downloader.downloadAsTemp(key);

			boolean hasPet = aiService.hasPet(file.getAbsolutePath());
			file.delete();

			return hasPet;

		} catch (Exception e) {
			System.out.println("❌ AI 분석 오류: " + e.getMessage());
			return false;  // 실패 시 false 보장
		}
	}
}