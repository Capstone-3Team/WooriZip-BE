package org.scoula.backend.domain.VideoAnswer.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerResponse;
import org.scoula.backend.domain.VideoAnswer.service.VideoAnswerService;
import org.scoula.backend.domain.VideoAnswer.service.PetShortsAsyncService;   // 🔥 추가
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/video-answer")
@RequiredArgsConstructor
@Tag(name = "VideoAnswer", description = "영상 답변 API")
public class VideoAnswerController {

	private final VideoAnswerService videoAnswerService;
	private final PetShortsAsyncService petShortsAsyncService;   // 🔥 추가 (오류 해결 핵심)

	// ==========================
	// 영상 업로드
	// ==========================
	@PostMapping
	@Operation(
		summary = "영상 답변 업로드",
		description = "사용자가 영상 답변 내용을 업로드합니다."
	)
	public VideoAnswer createVideo(
		@RequestPart("video") MultipartFile videoFile,
		@RequestParam("questionId") Long questionId,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		return videoAnswerService.createVideoAnswer(videoFile, questionId, email);
	}

	// 주차별 영상 조회
	@GetMapping
	public List<VideoAnswerResponse> getAnswers(
		@RequestParam Long questionId,
		@AuthenticationPrincipal User user
	) {
		return videoAnswerService.getAnswers(questionId, user.getUsername());
	}

	// 수정
	@PutMapping("/{id}")
	public VideoAnswer updateVideo(
		@PathVariable Long id,
		@RequestBody VideoAnswerRequest request,
		@AuthenticationPrincipal User user
	) {
		return videoAnswerService.updateVideoAnswer(id, request, user.getUsername());
	}

	// 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteVideo(
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		videoAnswerService.deleteVideoAnswer(id, user.getUsername());
		return ResponseEntity.ok("영상 답변이 성공적으로 삭제되었습니다.");
	}

	@GetMapping("/{id}")
	public VideoAnswerResponse getVideo(
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		return videoAnswerService.getVideoById(id, user.getUsername());
	}

	// 전체 영상 조회
	@GetMapping("/all")
	public List<VideoAnswerResponse> getAllAnswers(@AuthenticationPrincipal User user) {
		return videoAnswerService.getAllAnswers(user.getUsername());
	}

	// ================================
	// 🔥 반려동물 숏츠 생성 트리거 API
	// ================================
	@PostMapping("/pet/shorts")
	public ResponseEntity<?> createShorts(
		@RequestParam("answerId") Long answerId
	) {
		petShortsAsyncService.createPetShorts(answerId);
		return ResponseEntity.ok("🚀 숏츠 생성 시작!");
	}


}
