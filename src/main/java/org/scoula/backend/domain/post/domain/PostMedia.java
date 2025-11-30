package org.scoula.backend.domain.post.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMedia {
	private Long id;
	private Long postId;
	private String mediaUrl;
}
