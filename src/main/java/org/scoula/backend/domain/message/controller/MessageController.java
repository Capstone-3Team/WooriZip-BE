package org.scoula.backend.domain.message.controller;


import java.util.List;

import org.scoula.backend.domain.message.dto.MessageDetailResponse;
import org.scoula.backend.domain.message.dto.MessageReceivedResponse;
import org.scoula.backend.domain.message.dto.MessageSendRequest;
import org.scoula.backend.domain.message.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "Message", description = "쪽지 API")
public class MessageController {

	private final MessageService messageService;

	@PostMapping
	@Operation(summary = "쪽지 보내기")
	public ResponseEntity<Long> sendMessage(
		@RequestBody MessageSendRequest request,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		Long messageId = messageService.sendMessage(email, request.getReceiverId(), request.getContent());
		return ResponseEntity.ok(messageId);
	}

	@GetMapping("/received")
	@Operation(summary = "받은 쪽지 조회")
	public ResponseEntity<List<MessageReceivedResponse>> getReceivedMessages(
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		return ResponseEntity.ok(messageService.getMyReceivedMessages(email));
	}

	@GetMapping("/{id}")
	@Operation(summary = "쪽지 상세 조회")
	public ResponseEntity<MessageDetailResponse> readMessage(@PathVariable Long id) {
		return ResponseEntity.ok(messageService.readMessage(id));
	}


}
