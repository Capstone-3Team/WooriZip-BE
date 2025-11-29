package org.scoula.backend.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageDetailResponse {
	private Long id;
	private String senderNickname;
	private String content;
	private LocalDateTime createdAt;
}
