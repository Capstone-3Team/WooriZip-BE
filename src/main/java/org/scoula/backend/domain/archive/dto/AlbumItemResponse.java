package org.scoula.backend.domain.archive.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlbumItemResponse {

	private String type;            // IMAGE
	private String url;             // S3 media URL
	private LocalDateTime createdAt;
	private String profileImageUrl; // 업로더 프로필 이미지
}
