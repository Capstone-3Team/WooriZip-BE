package org.scoula.backend.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.PostComment;
import org.scoula.backend.domain.post.service.PostCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/comments")
@RequiredArgsConstructor
public class PostCommentController {

	private final PostCommentService postCommentService;

	@PostMapping
	public ResponseEntity<String> addComment(
		@RequestParam Long postId,
		@RequestParam Long familyMemberId,
		@RequestParam String content) {

		PostComment comment = PostComment.builder()
			.postId(postId)
			.familyMemberId(familyMemberId)
			.content(content)
			.build();

		postCommentService.addComment(comment);
		return ResponseEntity.ok("댓글 등록 완료");
	}

	@GetMapping("/{postId}")
	public ResponseEntity<List<PostComment>> getComments(@PathVariable Long postId) {
		return ResponseEntity.ok(postCommentService.getComments(postId));
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<String> deleteComment(
		@PathVariable Long commentId,
		@RequestParam Long familyMemberId) {
		postCommentService.deleteComment(commentId, familyMemberId);
		return ResponseEntity.ok("댓글 삭제 완료");
	}
}
