package org.scoula.backend.domain.post.controller;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.PostComment;
import org.scoula.backend.domain.post.service.PostCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-comments")
@RequiredArgsConstructor
public class PostCommentController {

	private final PostCommentService postCommentService;

	// ✅ 댓글 등록
	@PostMapping
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
	public ResponseEntity<List<PostComment>> getCommentsByPostId(@PathVariable Long postId) {
		List<PostComment> comments = postCommentService.getCommentsByPostId(postId);
		return ResponseEntity.ok(comments);
	}

	// ✅ 전체 댓글 조회
	@GetMapping
	public ResponseEntity<List<PostComment>> getAllComments() {
		List<PostComment> comments = postCommentService.getAllComments();
		return ResponseEntity.ok(comments);
	}

	// ✅ 댓글 수정
	@PutMapping("/{commentId}")
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
	public ResponseEntity<String> deleteComment(
		@PathVariable Long commentId,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		postCommentService.deleteCommentByEmail(commentId, email);
		return ResponseEntity.ok("댓글 삭제 완료");
	}
}
