package org.scoula.backend.domain.VideoAnswer.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerResponse;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.service.AiAnalysisService;
import org.scoula.backend.global.s3.S3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class VideoAnswerService {

	private final VideoAnswerRepository videoAnswerRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final AiAnalysisService aiAnalysisService;
	private final PetShortsAsyncService petShortsAsyncService;
	private final S3Uploader s3Uploader;
	private final AsyncShortsExecutor asyncShortsExecutor;

	@Transactional
	public VideoAnswer createVideoAnswer(MultipartFile videoFile, Long questionId, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 1) S3 업로드
		String s3VideoUrl = s3Uploader.upload(videoFile, "video-answers");

		// 2) AI 처리용 temp 파일 생성
		File tempFile;
		try {
			tempFile = File.createTempFile("video_", ".mp4");
			videoFile.transferTo(tempFile);
		} catch (IOException e) {
			throw new RuntimeException("임시 파일 생성 실패", e);
		}


		// 3) 병렬 AI 요청
		CompletableFuture<Map<String, Object>> thumbnailFuture =
			CompletableFuture.supplyAsync(() -> aiAnalysisService.requestThumbnail(tempFile));

		CompletableFuture<Map<String, Object>> sttFuture =
			CompletableFuture.supplyAsync(() -> aiAnalysisService.requestStt(tempFile));

		CompletableFuture.allOf(thumbnailFuture, sttFuture).join();

		Map<String, Object> thumbnail = thumbnailFuture.join();
		Map<String, Object> stt = sttFuture.join();

		// 4) DB 저장
		VideoAnswer saved = videoAnswerRepository.save(
			VideoAnswer.builder()
				.questionId(questionId)
				.familyMemberId(member.getId())
				.familyId(member.getFamilyId().longValue())
				.videoUrl(s3VideoUrl)
				.thumbnailUrl((String) thumbnail.get("image_base64"))
				.title((String) stt.get("title"))
				.summary((String) stt.get("summary"))
				.shortsStatus("PENDING")
				.createdAt(LocalDateTime.now())
				.build()
		);

		// 5) Commit 이후 숏츠 비동기 실행
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				asyncShortsExecutor.run(saved.getId());
			}
		});

		return saved;
	}



	public List<VideoAnswerResponse> getAnswers(Long questionId, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Long familyId = member.getFamilyId().longValue();
		List<VideoAnswer> answers = videoAnswerRepository.findByQuestionIdAndFamilyId(questionId, familyId);

		return answers.stream()
			.map(answer -> {
				FamilyMember uploader = familyMemberRepository.findById(answer.getFamilyMemberId())
					.orElseThrow(() -> new IllegalArgumentException("업로더 정보를 찾을 수 없습니다."));

				boolean isOwner = uploader.getEmail().equals(email);

				return VideoAnswerResponse.builder()
					.id(answer.getId())
					.questionId(answer.getQuestionId())
					.familyMemberId(answer.getFamilyMemberId())
					.familyId(answer.getFamilyId())
					.videoUrl(answer.getVideoUrl())
					.thumbnailUrl(answer.getThumbnailUrl())
					.title(answer.getTitle())
					.summary(answer.getSummary())
					.nickname(uploader.getNickname())
					.profileImageUrl(uploader.getProfileImage())
					.isOwner(isOwner)
					.createdAt(answer.getCreatedAt())
					.build();
			})
			.toList();
	}

	@Transactional
	public VideoAnswer updateVideoAnswer(Long id, VideoAnswerRequest request, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		if (!answer.getFamilyMemberId().equals(member.getId())) {
			throw new SecurityException("본인의 영상만 수정할 수 있습니다.");
		}

		// 🔥 필드별 부분 수정 (null 값은 무시)
		if (request.getVideoUrl() != null) {
			answer.setVideoUrl(request.getVideoUrl());
		}
		if (request.getThumbnailUrl() != null) {
			answer.setThumbnailUrl(request.getThumbnailUrl());
		}
		if (request.getTitle() != null) {
			answer.setTitle(request.getTitle());
		}
		if (request.getSummary() != null) {
			answer.setSummary(request.getSummary());
		}

		return videoAnswerRepository.save(answer);
	}

	@Transactional
	public void deleteVideoAnswer(Long id, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		if (!answer.getFamilyMemberId().equals(member.getId())) {
			throw new SecurityException("본인의 영상만 삭제할 수 있습니다.");
		}

		videoAnswerRepository.delete(answer);
	}

	public VideoAnswerResponse getVideoById(Long id, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		FamilyMember uploader = familyMemberRepository.findById(answer.getFamilyMemberId())
			.orElseThrow(() -> new IllegalArgumentException("업로더 정보를 찾을 수 없습니다."));

		boolean isOwner = uploader.getEmail().equals(email);

		return VideoAnswerResponse.builder()
			.id(answer.getId())
			.questionId(answer.getQuestionId())
			.familyMemberId(answer.getFamilyMemberId())
			.familyId(answer.getFamilyId())
			.videoUrl(answer.getVideoUrl())
			.thumbnailUrl(answer.getThumbnailUrl())
			.title(answer.getTitle())
			.summary(answer.getSummary())
			.nickname(uploader.getNickname())
			.profileImageUrl(uploader.getProfileImage())
			.isOwner(isOwner)
			.createdAt(answer.getCreatedAt())
			.build();
	}

	public List<VideoAnswerResponse> getAllAnswers(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Long familyId = member.getFamilyId().longValue();

		// 모든 가족 영상 조회
		List<VideoAnswer> result = videoAnswerRepository.findByFamilyId(familyId);

		return result.stream()
			.map(answer -> {
				FamilyMember uploader = familyMemberRepository.findById(answer.getFamilyMemberId())
					.orElseThrow(() -> new IllegalArgumentException("업로더 정보를 찾을 수 없습니다."));

				boolean isMine = uploader.getEmail().equals(email);

				return VideoAnswerResponse.builder()
					.id(answer.getId())
					.familyId(answer.getFamilyId())
					.familyMemberId(answer.getFamilyMemberId())
					.nickname(uploader.getNickname())
					.profileImageUrl(uploader.getProfileImage())
					.videoUrl(answer.getVideoUrl())
					.thumbnailUrl(answer.getThumbnailUrl())
					.createdAt(answer.getCreatedAt())
					.questionId(answer.getQuestionId())
					.isOwner(isMine)
					.build();
			})
			.toList();
	}


}
