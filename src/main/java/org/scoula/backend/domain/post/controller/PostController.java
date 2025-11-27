package org.scoula.backend.domain.post.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.nimbusds.jwt.JWT;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Tag(name = "Post", description = "일상 피드 API")
public class PostController {
	private final PostService postService;
	// 게시글 등록
	@PostMapping
	@Operation(
		summary = "일상 피드 생성",
		description = "이미지 파일(MultipartFile)과 설명(description)을 포함하여 게시글을 생성합니다. "
	)
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
	@Operation(
		summary = "전체 일상 피드 조회",
		description = "로그인한 사용자의 이메일(JWT 기준)에 해당하는 전체 게시글 목록을 조회합니다."
	)
	public ResponseEntity<List<Post>> getAllPosts(@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		List<Post> posts = postService.getAllPostsByEmail(email);
		return ResponseEntity.ok(posts);
	}
	// 게시글 수정
	@PutMapping("/{postId}")
	@Operation(
		summary = "일상 피드 수정",
		description = "postId에 해당하는 게시글의 설명(description)만 수정합니다. 게시글 작성자와 JWT 사용자 이메일이 일치해야 수정이 가능합니다."
	)
	public ResponseEntity<String> updatePost(
		@PathVariable Long postId,
		@RequestParam String description,
		@AuthenticationPrincipal User user) {

		String email = user.getUsername();
		postService.updatePostByEmail(postId, email, description);
		return ResponseEntity.ok("게시글 수정 완료");
	}


	// 게시글 삭제
	@DeleteMapping("/{postId}")
	@Operation(
		summary = "일상 피드 삭제",
		description = "postId에 해당하는 게시글을 삭제합니다. 게시글 작성자와 JWT 사용자 이메일이 일치해야 삭제가 가능합니다."
	)
	public ResponseEntity<String> deletePost(@PathVariable Long postId,
		@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		postService.deletePostByEmail(postId, email);
		return ResponseEntity.ok("게시글 삭제 완료");
	}
}
