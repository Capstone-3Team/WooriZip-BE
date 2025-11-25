package org.scoula.backend.domain.VideoAnswer.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/video-answer-comment")
@RequiredArgsConstructor
@Tag(name = "VideoAnswerComment", description = "영상 답변 댓글 API")
public class VideoAnswerCommentController {

	private final VideoAnswerCommentService commentService;

	// 생성
	@PostMapping
	@Operation(
		summary = "영상 답변 댓글 생성",
		description = "특정 영상 답변에 대해 새로운 댓글을 작성합니다."
	)
	public VideoAnswerComment createComment(
		@RequestBody VideoAnswerCommentRequest request,
		@AuthenticationPrincipal User user) {

		return commentService.createComment(request, user.getUsername());
	}

	// 조회
	@GetMapping
	@Operation(
		summary = "영상 답변 댓글 조회",
		description = "특정 영상 답변(videoAnswerId)에 달린 모든 댓글을 조회합니다."
	)
	public List<VideoAnswerComment> getComments(@RequestParam Long videoAnswerId) {
		return commentService.getComments(videoAnswerId);
	}

	// 수정
	@PutMapping("/{id}")
	@Operation(
		summary = "영상 답변 댓글 수정",
		description = "댓글 ID에 해당하는 댓글을 수정합니다. 댓글 작성자와 JWT 사용자 이메일이 일치해야 수정이 가능합니다."
	)
	public VideoAnswerComment updateComment(
		@PathVariable Long id,
		@RequestBody VideoAnswerCommentRequest request,
		@AuthenticationPrincipal User user) {

		return commentService.updateComment(id, request, user.getUsername());
	}

	// 삭제
	@DeleteMapping("/{id}")
	@Operation(
		summary = "영상 답변 댓글 삭제",
		description = "댓글 ID에 해당하는 댓글을 삭제합니다. 본인 댓글만 삭제할 수 있습니다."
	)
	public ResponseEntity<String> deleteComment(
		@PathVariable Long id,
		@AuthenticationPrincipal User user) {

		commentService.deleteComment(id, user.getUsername());
		return ResponseEntity.ok("댓글이 삭제되었습니다.");
	}
}
