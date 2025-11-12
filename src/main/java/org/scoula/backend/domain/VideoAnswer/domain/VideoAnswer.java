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

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
