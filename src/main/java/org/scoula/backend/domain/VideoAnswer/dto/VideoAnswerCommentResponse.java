package org.scoula.backend.domain.VideoAnswer.dto;


import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoAnswerCommentResponse {

	private Long id;
	private Long videoAnswerId;
	private Long familyMemberId;
	private Long familyId;

	private String content;
	private String nickname;
	private String profileImageUrl;

	private boolean isOwner;
	private LocalDateTime createdAt;
}
