package org.scoula.backend.domain.archive.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.archive.dto.ArchiveMainResponse;
import org.scoula.backend.domain.archive.service.ArchiveMainService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/archive")
public class ArchiveMainController {

	private final ArchiveMainService archiveMainService;

	@GetMapping("/main")
	public ArchiveMainResponse getArchiveMain(@AuthenticationPrincipal User user) {
		return archiveMainService.getArchiveMain(user.getUsername());
	}
}
