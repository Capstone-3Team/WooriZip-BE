package org.scoula.backend.domain.archive.service;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.archive.domain.GalleryItem;
import org.scoula.backend.domain.archive.repository.GalleryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryService {

	private final GalleryItemRepository repository;

	public List<GalleryItem> getAllItems() {
		return repository.findAll();
	}

	public GalleryItem saveItem(String url, String type) {

		// 만약 url이 파일명만 넘어온다면
		String publicUrl = "http://localhost:8080/files/" + url;

		return repository.save(
			GalleryItem.builder()
				.fileUrl(publicUrl)
				.fileType(type)
				.build()
		);
	}

}
