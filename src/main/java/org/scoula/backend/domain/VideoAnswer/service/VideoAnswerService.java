package org.scoula.backend.domain.VideoAnswer.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.service.AiAnalysisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
	public VideoAnswer createVideoAnswer(VideoAnswerRequest request, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		File videoFile = new File(request.getVideoUrl());
		if (!videoFile.exists()) {
			throw new IllegalArgumentException("비디오 파일을 찾을 수 없습니다: " + request.getVideoUrl());
		}

		// 1) 병렬 AI 요청
		CompletableFuture<Map<String, Object>> thumbnailFuture =
			CompletableFuture.supplyAsync(() -> aiAnalysisService.requestThumbnail(videoFile));

		CompletableFuture<Map<String, Object>> sttFuture =
			CompletableFuture.supplyAsync(() -> aiAnalysisService.requestStt(videoFile));

		CompletableFuture.allOf(thumbnailFuture, sttFuture).join();

		Map<String, Object> thumbnail = thumbnailFuture.join();
		Map<String, Object> stt = sttFuture.join();

		VideoAnswer answer = VideoAnswer.builder()
			.questionId(request.getQuestionId())
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId().longValue())
			.videoUrl(request.getVideoUrl())
			.thumbnailUrl((String) thumbnail.get("image_base64"))
			.title((String) stt.get("title"))
			.summary((String) stt.get("summary"))
			.shortsStatus("PENDING")
			.createdAt(LocalDateTime.now())
			.build();

		VideoAnswer saved = videoAnswerRepository.save(answer);

		// 🔥 트랜잭션 commit 이후에 비동기 실행
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				petShortsAsyncService.processPetShorts(saved.getId());
			}
		});

		return saved;
	}


	public List<VideoAnswer> getAnswers(Long questionId, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Long familyId = member.getFamilyId().longValue();
		return videoAnswerRepository.findByQuestionIdAndFamilyId(questionId, familyId);
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

		answer.setVideoUrl(request.getVideoUrl());
		answer.setThumbnailUrl(request.getThumbnailUrl());
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


	public VideoAnswer getVideoById(Long id, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		if (!answer.getFamilyId().equals(member.getFamilyId().longValue())) {
			throw new SecurityException("해당 영상에 접근할 수 없습니다.");
		}

		return answer;
	}
}
