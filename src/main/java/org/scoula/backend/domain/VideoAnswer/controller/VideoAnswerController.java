package org.scoula.backend.domain.VideoAnswer.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.service.VideoAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video-answers")
@RequiredArgsConstructor
public class VideoAnswerController {

	private final VideoAnswerService videoAnswerService;

	// ✅ 영상 업로드
	@PostMapping
	public VideoAnswer createVideo(@RequestBody VideoAnswerRequest request,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername(); // ✅ JWT에서 이메일 추출
		return videoAnswerService.createVideoAnswer(request, email);
	}

	// ✅ 주차별 영상 답변 조회
	@GetMapping
	public List<VideoAnswer> getAnswers(@RequestParam Long questionId,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		return videoAnswerService.getAnswers(questionId, email);
	}


	// ✅ 수정
	@PutMapping("/{id}")
	public VideoAnswer updateVideo(@PathVariable Long id,
		@RequestBody VideoAnswerRequest request,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		return videoAnswerService.updateVideoAnswer(id, request, email);
	}

	// ✅ 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteVideo(@PathVariable Long id,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		videoAnswerService.deleteVideoAnswer(id, email);
		return ResponseEntity.ok("영상 답변이 성공적으로 삭제되었습니다.");
	}

}
