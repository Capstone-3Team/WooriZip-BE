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
import java.net.URL;
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


	/* ----------------------------------------------------
	 * 🔥 URL → S3 Key 추출 (정확 버전)
	 * ---------------------------------------------------- */
	private String extractKeyFromUrl(String url) {
		try {
			URL u = new URL(url);
			String path = u.getPath(); // /post-images/abcd.jpg
			return path.startsWith("/") ? path.substring(1) : path;
		} catch (Exception e) {
			throw new RuntimeException("Invalid S3 URL: " + url);
		}
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

	public List<PetGalleryItemResponse> getPetPosts(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Integer familyId = member.getFamilyId();
		Long memberId = member.getId();

		List<PetGalleryItemResponse> result = new ArrayList<>();

		// 1) POST DAILY 필터링
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		for (PostResponse post : posts) {
			List<String> mediaUrls = postMapper.findMediaByPostId(post.getId());

			for (String url : mediaUrls) {

				PetMedia cached = petMediaMapper.findByMediaUrl(url);

				if (cached == null) {
					boolean isPet = aiService.hasPetFromUrl(url);

					PetMedia p = new PetMedia();
					p.setMediaUrl(url);
					p.setPostId(post.getId());
					p.setFamilyMemberId(post.getFamilyMemberId());
					p.setIsPet(isPet);

					petMediaMapper.insertPetMedia(p);
					cached = p;
				}

				if (!Boolean.TRUE.equals(cached.getIsPet()))
					continue;

				FamilyMember writer = familyMemberRepository.findById(post.getFamilyMemberId())
					.orElseThrow();

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

		// 2) SHORTS (DONE 상태만)
		List<VideoAnswer> shorts = videoAnswerRepository.findByFamilyIdAndShortsStatus(
			familyId.longValue(), "DONE"
		);

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

		result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		return result;
	}
}