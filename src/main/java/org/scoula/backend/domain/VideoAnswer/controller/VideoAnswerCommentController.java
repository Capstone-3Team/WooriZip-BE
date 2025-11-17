package org.scoula.backend.domain.VideoAnswer.controller;
import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswerComment;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerCommentRequest;
import org.scoula.backend.domain.VideoAnswer.service.VideoAnswerCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video-answer-comments")
@RequiredArgsConstructor
public class VideoAnswerCommentController {

	private final VideoAnswerCommentService commentService;

	// 생성
	@PostMapping
	public VideoAnswerComment createComment(
		@RequestBody VideoAnswerCommentRequest request,
		@AuthenticationPrincipal User user) {

		return commentService.createComment(request, user.getUsername());
	}

	// 조회
	@GetMapping
	public List<VideoAnswerComment> getComments(@RequestParam Long videoAnswerId) {
		return commentService.getComments(videoAnswerId);
	}

	// 수정
	@PutMapping("/{id}")
	public VideoAnswerComment updateComment(
		@PathVariable Long id,
		@RequestBody VideoAnswerCommentRequest request,
		@AuthenticationPrincipal User user) {

		return commentService.updateComment(id, request, user.getUsername());
	}

	// 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteComment(
		@PathVariable Long id,
		@AuthenticationPrincipal User user) {

		commentService.deleteComment(id, user.getUsername());
		return ResponseEntity.ok("댓글이 삭제되었습니다.");
	}
}
