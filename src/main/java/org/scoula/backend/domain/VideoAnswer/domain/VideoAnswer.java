package org.scoula.backend.domain.VideoAnswer.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long questionId;
	private Long familyMemberId;
	private Long familyId;

	private String videoUrl;
	private String thumbnailUrl;

	private String title;
	private String summary;

	private String shortsUrl;       // 추가됨
	private String shortsStatus;    // PENDING, PROCESSING, DONE, FAILED

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
