package org.scoula.backend.domain.VideoAnswer.dto;


import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoAnswerResponse {

	private Long id;
	private Long questionId;
	private Long familyMemberId;
	private Long familyId;

	private String videoUrl;
	private String thumbnailUrl;

	private String title;
	private String summary;

	private String nickname;         // 업로더 별명
	private String profileImageUrl;  // 업로더 프로필 사진

	private boolean isOwner;         // 수정/삭제 권한 여부

	private LocalDateTime createdAt;
}
