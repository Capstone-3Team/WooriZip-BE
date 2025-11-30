package org.scoula.backend.domain.VideoAnswer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoAnswerRequest {
	private Long questionId;
	@Schema(hidden = true)
	private Long familyMemberId;
	@Schema(hidden = true)
	private Long familyId;

	private String videoUrl;
	@Schema(hidden = true)
	private String thumbnailUrl;
	@Schema(hidden = true)
	private String title;          // AI 제목
	@Schema(hidden = true)
	private String summary;        // AI 요약
}
