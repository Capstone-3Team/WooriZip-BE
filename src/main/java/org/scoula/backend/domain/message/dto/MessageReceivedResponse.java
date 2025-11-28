package org.scoula.backend.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageReceivedResponse {
	private Long id;               // messageId
	private String senderNickname; // 보낸 사람 별명
	private Boolean isRead;        //  읽음 여부
	private LocalDateTime createdAt;
}
