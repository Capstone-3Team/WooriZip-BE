package org.scoula.backend.domain.archive.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.domain.archive.dto.AlbumItemResponse;
import org.scoula.backend.domain.archive.dto.MemberAlbumItemResponse;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final FamilyMemberRepository familyMemberRepository;
	private final PostMapper postMapper;
	private final VideoAnswerRepository videoAnswerRepository;


	// ⭐ 일상 기록 보관함 조회
	public List<AlbumItemResponse> getDailyAlbum(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Integer familyId = member.getFamilyId();

		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		List<AlbumItemResponse> result = new ArrayList<>();

		for (PostResponse post : posts) {

			FamilyMember uploader = familyMemberRepository.findById(post.getFamilyMemberId())
				.orElseThrow(() -> new IllegalArgumentException("업로더 정보를 찾을 수 없습니다."));

			List<String> mediaList = postMapper.findMediaByPostId(post.getId());

			for (String url : mediaList) {

				result.add(
					AlbumItemResponse.builder()
						.type(detectMediaType(url))                // ⭐ 영상이면 VIDEO로!
						.url(url)
						.createdAt(post.getCreatedAt())
						.profileImageUrl(uploader.getProfileImage())
						.build()
				);
			}
		}

		return result;
	}



	private String detectMediaType(String url) {
		String lower = url.toLowerCase();
		if (lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".avi"))
			return "VIDEO";
		return "IMAGE";
	}

	// ⭐ 멤버별 보관함
	public List<MemberAlbumItemResponse> getMemberAlbum(Long memberId, String email) {

		FamilyMember target = familyMemberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("멤버 정보를 찾을 수 없습니다."));

		List<MemberAlbumItemResponse> result = new ArrayList<>();

		/* -------------------------------------------------------
		 * 1) 영상답변(VideoAnswer)
		 * ------------------------------------------------------- */
		List<VideoAnswer> videoAnswers = videoAnswerRepository.findByFamilyMemberId(memberId);

		for (VideoAnswer v : videoAnswers) {
			result.add(
				MemberAlbumItemResponse.builder()
					.type("VIDEO_ANSWER")
					.url(v.getVideoUrl())
					.createdAt(v.getCreatedAt())
					.profileImageUrl(target.getProfileImage())
					.nickname(target.getNickname())
					.build()
			);
		}

		/* -------------------------------------------------------
		 * 2) 일상피드 (사진/영상)
		 * ------------------------------------------------------- */
		List<PostResponse> posts = postMapper.findPostsByMemberId(memberId);

		for (PostResponse post : posts) {

			List<String> mediaList = postMapper.findMediaByPostId(post.getId());

			for (String url : mediaList) {

				result.add(
					MemberAlbumItemResponse.builder()
						.type(detectMediaType(url))  // IMAGE or VIDEO
						.url(url)
						.createdAt(post.getCreatedAt())
						.profileImageUrl(target.getProfileImage())
						.nickname(target.getNickname())
						.build()
				);
			}
		}

		/* -------------------------------------------------------
		 * 3) 최신순 정렬
		 * ------------------------------------------------------- */
		result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

		return result;
	}
}
