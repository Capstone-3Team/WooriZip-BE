package org.scoula.backend.domain.message.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long familyMemberId; // sender의 familyMemberId
	private Integer familyId;

	private Long senderId;
	private Long receiverId;

	private Long replyToId;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Column(name = "is_read")
	private Boolean isRead;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
