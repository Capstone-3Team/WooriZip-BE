package org.scoula.backend.domain.message.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.message.domain.Message;
import org.scoula.backend.domain.message.dto.MessageDetailResponse;
import org.scoula.backend.domain.message.dto.MessageFamilyMemberResponse;
import org.scoula.backend.domain.message.dto.MessageReceivedResponse;
import org.scoula.backend.domain.message.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;
	private final FamilyMemberRepository familyMemberRepository;

	// 1️⃣ 쪽지 전송
	public Long sendMessage(String email, Long receiverId, String content) {

		FamilyMember sender = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// ✨ receiver가 같은 가족인지 검증
		FamilyMember receiver = familyMemberRepository.findById(receiverId)
			.orElseThrow(() -> new IllegalArgumentException("받는 사람을 찾을 수 없습니다."));

		if (!receiver.getFamilyId().equals(sender.getFamilyId())) {
			throw new IllegalArgumentException("같은 가족 구성원에게만 쪽지를 보낼 수 있습니다.");
		}

		Message message = Message.builder()
			.senderId(sender.getId())
			.receiverId(receiverId)
			.familyId(sender.getFamilyId())
			.familyMemberId(sender.getId())
			.replyToId(null)
			.content(content)
			.isRead(false)
			.createdAt(LocalDateTime.now())
			.build();

		return messageRepository.save(message).getId();
	}

	// 2️⃣ 받은 쪽지 목록 조회 (닉네임 + 보낸시간만)
	public List<MessageReceivedResponse> getMyReceivedMessages(String email) {

		FamilyMember me = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		return messageRepository.findByReceiverIdOrderByCreatedAtDesc(me.getId())
			.stream()
			.map(msg -> {

				FamilyMember sender = familyMemberRepository.findById(msg.getSenderId())
					.orElseThrow(() -> new IllegalArgumentException("보낸 사람 정보를 찾을 수 없습니다."));

				return new MessageReceivedResponse(
					msg.getId(),
					sender.getNickname(),
					msg.getIsRead(),   // ⭐ 읽음 여부 전달!
					msg.getCreatedAt()
				);
			})
			.toList();
	}


	// 3️⃣ 쪽지 상세 조회
	public MessageDetailResponse readMessage(Long messageId) {

		Message msg = messageRepository.findById(messageId)
			.orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));

		// 읽음 처리
		msg.setIsRead(true);
		messageRepository.save(msg);

		FamilyMember sender = familyMemberRepository.findById(msg.getSenderId())
			.orElseThrow(() -> new IllegalArgumentException("보낸 사람 정보를 찾을 수 없습니다."));

		return new MessageDetailResponse(
			msg.getId(),
			sender.getNickname(),
			msg.getContent(),
			msg.getCreatedAt()
		);
	}

	public List<MessageFamilyMemberResponse> getFamilyMembers(String email) {

		FamilyMember me = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		List<FamilyMember> members =
			familyMemberRepository.findByFamilyId(me.getFamilyId());

		// 자기 자신은 제외하고 리스트 반환
		return members.stream()
			.filter(m -> !m.getId().equals(me.getId()))
			.map(m -> new MessageFamilyMemberResponse(
				m.getId(),
				m.getNickname(),
				m.getProfileImage()
			))
			.toList();
	}



}
