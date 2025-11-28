package org.scoula.backend.domain.archive.controller;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.archive.service.PetPostService;
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
public class PetPostController {

	private final PetPostService petPostService;

	@GetMapping("/pet")
	public ResponseEntity<?> getPetPosts(@AuthenticationPrincipal User user) {
		String email = user.getUsername();
		List<Post> petPosts = petPostService.getPetPosts(email);
		return ResponseEntity.ok(petPosts);
	}
}
