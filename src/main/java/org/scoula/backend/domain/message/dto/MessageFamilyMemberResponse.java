package org.scoula.backend.domain.message.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageFamilyMemberResponse {
	private Long id;            // familyMemberId
	private String nickname;    // 별명
	private String profileImage;
}
