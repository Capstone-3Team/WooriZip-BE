package org.scoula.backend.domain.VideoAnswer.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoAnswerCommentRequest {
	private Long videoAnswerId;
	private String content;
}
