package org.scoula.backend.domain.post.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentResponse {
	private Long id;
	private Long postId;
	private Long familyMemberId;
	private String content;
	private LocalDateTime createdAt;

	private String writerNickname;  // 추가
	private String writerProfile;   // 추가
}
