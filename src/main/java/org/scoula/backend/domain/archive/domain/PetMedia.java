package org.scoula.backend.domain.archive.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PetMedia {
	private Long id;
	private String mediaUrl;
	private Long postId;
	private Long familyMemberId;
	private Boolean isPet;
	private LocalDateTime createdAt;
}
