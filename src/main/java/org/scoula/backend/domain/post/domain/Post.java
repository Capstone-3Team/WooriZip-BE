package org.scoula.backend.domain.post.domain;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
	private Long id;
	private Long familyMemberId;
	private Integer familyId;
	private String mediaUrl;
	private String description;
	private LocalDateTime createdAt;
}
