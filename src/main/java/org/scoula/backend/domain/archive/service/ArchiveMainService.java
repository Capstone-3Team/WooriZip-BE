package org.scoula.backend.domain.archive.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.domain.archive.dto.AlbumItemResponse;
import org.scoula.backend.domain.archive.dto.ArchiveMainResponse;
import org.scoula.backend.domain.archive.dto.PetGalleryItemResponse;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ArchiveMainService {

	private final FamilyMemberRepository familyMemberRepository;
	private final PostMapper postMapper;
	private final VideoAnswerRepository videoAnswerRepository;
	private final PetPostService petPostService;

	private String detectType(String url) {
		String lower = url.toLowerCase();
		if (lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".avi"))
			return "VIDEO";
		return "IMAGE";
	}

	public ArchiveMainResponse getArchiveMain(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Integer familyId = member.getFamilyId();

		return ArchiveMainResponse.builder()
			.daily(getDailyTop3(familyId))
			.member(getMemberTop3(familyId))
			.pet(getPetTop3(email))
			.build();
	}

	// ---------------- DAILY ---------------------
	private List<AlbumItemResponse> getDailyTop3(Integer familyId) {

		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);
		List<AlbumItemResponse> items = new ArrayList<>();

		for (PostResponse post : posts) {

			FamilyMember uploader = familyMemberRepository.findById(post.getFamilyMemberId())
				.orElseThrow(() -> new IllegalArgumentException("업로더 정보를 찾을 수 없습니다."));

			List<String> medias = postMapper.findMediaByPostId(post.getId());

			for (String url : medias) {

				items.add(
					AlbumItemResponse.builder()
						.type(detectType(url))
						.url(url)
						.thumbnailUrl(url)
						.createdAt(post.getCreatedAt())
						.nickname(uploader.getNickname())
						.profileImageUrl(uploader.getProfileImage())
						.build()
				);
			}
		}

		return items.stream()
			.sorted(Comparator.comparing(AlbumItemResponse::getCreatedAt).reversed())
			.limit(3)
			.toList();
	}

	// ---------------- MEMBER ---------------------
	private List<AlbumItemResponse> getMemberTop3(Integer familyId) {

		List<AlbumItemResponse> items = new ArrayList<>();

		// Post 포함
		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(familyId);

		for (PostResponse post : posts) {
			FamilyMember uploader = familyMemberRepository.findById(post.getFamilyMemberId())
				.orElseThrow();

			List<String> medias = postMapper.findMediaByPostId(post.getId());

			for (String url : medias) {

				items.add(
					AlbumItemResponse.builder()
						.type(detectType(url))
						.url(url)
						.thumbnailUrl(url)
						.createdAt(post.getCreatedAt())
						.nickname(uploader.getNickname())
						.profileImageUrl(uploader.getProfileImage())
						.build()
				);
			}
		}

		// VideoAnswer 포함
		List<VideoAnswer> answers = videoAnswerRepository.findByFamilyId(familyId.longValue());

		for (VideoAnswer v : answers) {

			FamilyMember uploader = familyMemberRepository.findById(v.getFamilyMemberId())
				.orElseThrow();

			items.add(
				AlbumItemResponse.builder()
					.type("VIDEO")
					.url(v.getVideoUrl())
					.thumbnailUrl(v.getThumbnailUrl())
					.createdAt(v.getCreatedAt())
					.nickname(uploader.getNickname())
					.profileImageUrl(uploader.getProfileImage())
					.build()
			);
		}

		return items.stream()
			.sorted(Comparator.comparing(AlbumItemResponse::getCreatedAt).reversed())
			.limit(3)
			.toList();
	}

	// ---------------- PET ---------------------
	private List<AlbumItemResponse> getPetTop3(String email) {

		List<PetGalleryItemResponse> petItems = petPostService.getPetPosts(email);

		return petItems.stream()
			.sorted(Comparator.comparing(PetGalleryItemResponse::getCreatedAt).reversed())
			.limit(3)
			.map(item ->
				AlbumItemResponse.builder()
					.type(item.getType())                 // POST | SHORTS
					.url(item.getMediaUrl())
					.thumbnailUrl(item.getThumbnailUrl())
					.createdAt(LocalDateTime.parse(item.getCreatedAt()))
					.nickname(item.getWriterNickname())
					.profileImageUrl(item.getWriterProfile())
					.build())
			.toList();
	}
}
