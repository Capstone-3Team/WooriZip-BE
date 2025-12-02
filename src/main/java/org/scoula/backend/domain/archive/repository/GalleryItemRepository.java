package org.scoula.backend.domain.archive.repository;

import org.scoula.backend.domain.archive.domain.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {
}
