package org.scoula.backend.domain.archive.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.archive.dto.PetGalleryItemResponse;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.archive.service.PetPostService;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Pet Post", description = "반려동물 모아보기 API")
public class PetPostController {

	private final PetPostService petPostService;

	@GetMapping("/pet")
	@Operation(
		summary = "반려동물 모아보기 조회",
		description = "반려동물 사진이나 영상의 목록을 조회합니다."
	)
	public ResponseEntity<?> getPetPosts(@AuthenticationPrincipal User user) {

		String email = user.getUsername();

		List<PetGalleryItemResponse> items = petPostService.getPetPosts(email);

		return ResponseEntity.ok(items);
	}
}