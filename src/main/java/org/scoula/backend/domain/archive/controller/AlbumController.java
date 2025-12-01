package org.scoula.backend.domain.archive.controller;


import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.archive.dto.AlbumItemResponse;
import org.scoula.backend.domain.archive.dto.MemberAlbumItemResponse;
import org.scoula.backend.domain.archive.service.AlbumService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	// ⭐ 일상 기록 보관함 (전체 피드 이미지/영상)
	@GetMapping("/daily")
	public List<AlbumItemResponse> getDailyAlbum(@AuthenticationPrincipal User user) {
		return albumService.getDailyAlbum(user.getUsername());
	}
	// ⭐ 멤버별 추억 보관함
	@GetMapping("/member/{memberId}")
	public List<MemberAlbumItemResponse> getMemberAlbum(
		@PathVariable Long memberId,
		@AuthenticationPrincipal User user
	) {
		return albumService.getMemberAlbum(memberId, user.getUsername());
	}

}
