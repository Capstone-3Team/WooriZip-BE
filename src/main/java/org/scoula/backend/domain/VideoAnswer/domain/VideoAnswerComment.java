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
@Table(name = "comment")
public class VideoAnswerComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long videoAnswerId;
	private Long familyMemberId;
	private Long familyId;

	@Column(columnDefinition = "TEXT")
	private String content;

	private LocalDateTime createdAt;
}
