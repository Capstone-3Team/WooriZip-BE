package org.scoula.backend.domain.VideoAnswer.controller;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.service.VideoAnswerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video-answers")
@RequiredArgsConstructor
public class VideoAnswerController {

	private final VideoAnswerService videoAnswerService;

	// ✅ 영상 업로드
	@PostMapping
	public VideoAnswer createVideo(@RequestBody VideoAnswerRequest request) {
		return videoAnswerService.createVideoAnswer(request);
	}

	// ✅ 주차별 영상 답변 조회
	@GetMapping
	public List<VideoAnswer> getAnswers(@RequestParam Long questionId, @RequestParam Long familyId) {
		return videoAnswerService.getAnswers(questionId, familyId);
	}

	// ✅ 수정
	@PutMapping("/{id}")
	public VideoAnswer updateVideo(@PathVariable Long id, @RequestBody VideoAnswerRequest request) {
		return videoAnswerService.updateVideoAnswer(id, request);
	}

	// ✅ 삭제
	@DeleteMapping("/{id}")
	public void deleteVideo(@PathVariable Long id) {
		videoAnswerService.deleteVideoAnswer(id);
	}
}
