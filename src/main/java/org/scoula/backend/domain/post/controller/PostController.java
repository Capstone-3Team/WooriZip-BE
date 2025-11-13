package org.scoula.backend.domain.post.controller;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;
	// 게시글 등록
	@PostMapping("/create")
	public ResponseEntity<String> createPost(
		@RequestParam(required = false) MultipartFile file,
		@RequestParam String description,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername(); // JWT에서 이메일 추출
		postService.createPostByEmail(email, file, description);
		return ResponseEntity.ok("게시글 등록 완료");
	}
	// 전체 게시글 조회
	@GetMapping
	public ResponseEntity<List<Post>> getAllPosts(@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		List<Post> posts = postService.getAllPostsByEmail(email);
		return ResponseEntity.ok(posts);
	}
	// 게시글 삭제
	@DeleteMapping("/{postId}")
	public ResponseEntity<String> deletePost(@PathVariable Long postId,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		postService.deletePostByEmail(postId, email);
		return ResponseEntity.ok("게시글 삭제 완료");
	}
}
