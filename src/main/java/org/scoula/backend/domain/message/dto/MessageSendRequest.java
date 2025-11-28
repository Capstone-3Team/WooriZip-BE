package org.scoula.backend.domain.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageSendRequest {
	private Long receiverId;  // 반드시 FamilyMember.id
	private String content;
}

