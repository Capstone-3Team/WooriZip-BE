package org.scoula.backend.domain.post.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.PostComment;
import org.scoula.backend.domain.post.service.PostCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post-comment")
@RequiredArgsConstructor
@Tag(name = "PostComment", description = "일상 피드 댓글 API")
public class PostCommentController {

	private final PostCommentService postCommentService;

	// ✅ 댓글 등록
	@PostMapping
	@Operation(
		summary = "일상 피드 댓글 생성",
		description = "postId와 content를 전달받아 새로운 댓글을 생성합니다. "
	)

	public ResponseEntity<String> createComment(
		@RequestParam Long postId,
		@RequestParam String content,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		postCommentService.createComment(email, postId, content);
		return ResponseEntity.ok("댓글 등록 완료");
	}

	// ✅ 특정 게시글 댓글 조회
	@GetMapping("/{postId}")
	@Operation(
		summary = "특정 일상 피드 댓글 조회",
		description = "postId에 해당하는 게시글에 달린 모든 댓글을 조회합니다."
	)
	public ResponseEntity<List<PostComment>> getCommentsByPostId(@PathVariable Long postId) {
		List<PostComment> comments = postCommentService.getCommentsByPostId(postId);
		return ResponseEntity.ok(comments);
	}

	// // ✅ 전체 댓글 조회
	// @GetMapping
	// @Operation(
	// 	summary = "일상 피드 전체 댓글 조회",
	// 	description = "작성된 모든 댓글 목록을 조회합니다."
	// )
	// public ResponseEntity<List<PostComment>> getAllComments() {
	// 	List<PostComment> comments = postCommentService.getAllComments();
	// 	return ResponseEntity.ok(comments);
	// }

	// ✅ 댓글 수정
	@PutMapping("/{commentId}")
	@Operation(
		summary = "일상 피드 댓글 수정",
		description = "commentId에 해당하는 댓글을 수정합니다. "
			+ "댓글 작성자와 JWT 사용자 이메일이 동일해야 수정이 가능합니다."
	)
	public ResponseEntity<String> updateComment(
		@PathVariable Long commentId,
		@RequestParam String content,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		postCommentService.updateComment(commentId, email, content);
		return ResponseEntity.ok("댓글 수정 완료");
	}

	// ✅ 댓글 삭제
	@DeleteMapping("/{commentId}")
	@Operation(
		summary = "일상 피드 댓글 삭제",
		description = "commentId에 해당하는 댓글을 삭제합니다. "
			+ "본인이 작성한 댓글만 삭제할 수 있습니다."
	)
	public ResponseEntity<String> deleteComment(
		@PathVariable Long commentId,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		postCommentService.deleteCommentByEmail(commentId, email);
		return ResponseEntity.ok("댓글 삭제 완료");
	}
}
