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
		GalleryItem item = GalleryItem.builder()
			.fileUrl(url)
			.fileType(type)
			.build();

		return repository.save(item);
	}
}
