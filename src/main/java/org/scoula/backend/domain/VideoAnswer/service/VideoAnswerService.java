package org.scoula.backend.domain.VideoAnswer.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerResponse;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.service.AiAnalysisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

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

	@Transactional
	public VideoAnswer createVideoAnswer(MultipartFile videoFile, Long questionId, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 1) 서버에 파일 저장
		String uploadDir = "/Users/juwon/Documents/4-2/Capstone/image/videouploads";  // 원하는 위치로 변경
		String fileName = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
		File dest = new File(uploadDir + fileName);

		try {
			videoFile.transferTo(dest);
		} catch (Exception e) {
			throw new RuntimeException("비디오 업로드 실패", e);
		}

		// 2) 병렬 AI 호출
		CompletableFuture<Map<String, Object>> thumbnailFuture =
			CompletableFuture.supplyAsync(() -> aiAnalysisService.requestThumbnail(dest));

		CompletableFuture<Map<String, Object>> sttFuture =
			CompletableFuture.supplyAsync(() -> aiAnalysisService.requestStt(dest));

		CompletableFuture.allOf(thumbnailFuture, sttFuture).join();

		Map<String, Object> thumbnail = thumbnailFuture.join();
		Map<String, Object> stt = sttFuture.join();

		// 3) DB 저장
		VideoAnswer answer = VideoAnswer.builder()
			.questionId(questionId)
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId().longValue())
			.videoUrl(dest.getAbsolutePath())     // 서버 저장 경로
			.thumbnailUrl((String) thumbnail.get("image_base64"))
			.title((String) stt.get("title"))
			.summary((String) stt.get("summary"))
			.shortsStatus("PENDING")
			.createdAt(LocalDateTime.now())
			.build();

		VideoAnswer saved = videoAnswerRepository.save(answer);

		// 4) 영상 쇼츠 변환 async 처리
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				petShortsAsyncService.processPetShorts(saved.getId());
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
			.build();
	}

}
