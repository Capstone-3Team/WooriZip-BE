package org.scoula.backend.domain.archive.controller;


import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.archive.domain.GalleryItem;
import org.scoula.backend.domain.archive.service.GalleryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {

	private final GalleryService galleryService;

	// 전체 목록 조회
	@GetMapping("/items")
	public List<GalleryItem> getGalleryItems() {
		return galleryService.getAllItems();
	}

	// (선택) 미디어 추가 API
	@PostMapping("/items")
	public GalleryItem addItem(
		@RequestParam String url,
		@RequestParam String type
	) {
		return galleryService.saveItem(url, type);
	}
}
