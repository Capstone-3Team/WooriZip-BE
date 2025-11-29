package org.scoula.backend.domain.post.dto;


import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
	private Long id;
	private Long familyMemberId;
	private Integer familyId;
	private String mediaUrl;
	private String description;
	private LocalDateTime createdAt;

	private String writerNickname;   // 추가
	private String writerProfile;    // 추가
	private Integer commentCount;    // 추가
}
