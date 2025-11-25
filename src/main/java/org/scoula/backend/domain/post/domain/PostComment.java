package org.scoula.backend.domain.post.domain;


import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostComment {
	private Long id;
	private Long postId;
	private Long familyMemberId;
	private String content;
	private LocalDateTime createdAt;
}
