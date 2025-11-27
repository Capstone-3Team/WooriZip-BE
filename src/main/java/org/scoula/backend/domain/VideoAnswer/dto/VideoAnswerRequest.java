package org.scoula.backend.domain.VideoAnswer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoAnswerRequest {
	private Long questionId;
	private Long familyMemberId;
	private Long familyId;
	private String videoUrl;
	private String thumbnailUrl;

	private String title;          // AI 제목
	private String summary;        // AI 요약
}
