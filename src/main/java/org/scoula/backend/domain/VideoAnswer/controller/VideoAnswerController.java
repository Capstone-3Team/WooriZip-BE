package org.scoula.backend.domain.VideoAnswer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerResponse;
import org.scoula.backend.domain.VideoAnswer.service.VideoAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/video-answer")
@RequiredArgsConstructor
@Tag(name = "VideoAnswer", description = "영상 답변 API")
public class VideoAnswerController {

	private final VideoAnswerService videoAnswerService;

	// ✅ 영상 업로드
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


	// ✅ 주차별 영상 답변 조회
	@GetMapping
	@Operation(
		summary = "주차별 영상 답변 조회",
		description = "questionId에 해당하는 영상 답변 목록을 반환합니다."
	)
	public List<VideoAnswerResponse> getAnswers(@RequestParam Long questionId,
		@AuthenticationPrincipal User user) {
		return videoAnswerService.getAnswers(questionId, user.getUsername());
	}


	// ✅ 수정
	@PutMapping("/{id}")
	@Operation(
		summary = "영상 답변 수정",
		description = "특정 영상 답변(ID)을 수정합니다. 수정 권한은 본인 영상 답변일 경우에만 허용됩니다."
	)
	public VideoAnswer updateVideo(@PathVariable Long id,
		@RequestBody VideoAnswerRequest request,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		return videoAnswerService.updateVideoAnswer(id, request, email);
	}

	// ✅ 삭제
	@DeleteMapping("/{id}")
	@Operation(
		summary = "영상 답변 삭제",
		description = "특정 영상 답변(ID)을 삭제합니다. 본인 영상 답변만 삭제할 수 있습니다."
	)
	public ResponseEntity<String> deleteVideo(@PathVariable Long id,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		videoAnswerService.deleteVideoAnswer(id, email);
		return ResponseEntity.ok("영상 답변이 성공적으로 삭제되었습니다.");
	}


	@GetMapping("/{id}")
	@Operation(
		summary = "단일 영상 답변 조회",
		description = "특정 영상 답변 ID 상세 정보 조회"
	)
	public VideoAnswerResponse getVideo(@PathVariable Long id, @AuthenticationPrincipal User user) {
		return videoAnswerService.getVideoById(id, user.getUsername());
	}

}
