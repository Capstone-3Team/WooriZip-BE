package org.scoula.backend.domain.archive.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PetGalleryItemResponse {

	private String type; // "POST" or "SHORTS"
	private Long id;

	private String mediaUrl;      // 이미지 또는 원본 영상 URL
	private String thumbnailUrl;  // 숏츠의 썸네일
	private String description;   // Post 설명
	private String title;         // Shorts 제목
	private String summary;       // Shorts 요약
	private String shortsUrl;     // Shorts 결과 URL

	private String writerNickname;
	private String writerProfile;
	private String createdAt;
}
