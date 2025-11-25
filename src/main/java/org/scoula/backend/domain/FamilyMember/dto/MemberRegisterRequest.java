package org.scoula.backend.domain.FamilyMember.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterRequest {
	private String email;
	private String familyName;   // 새 가족 생성 시 사용
	private String inviteCode;   // 기존 가족 참여 시 사용
	private String nickname;
	private String birth;
	private String phone;
	private String profileImage; // 선택사항
	private String password;

}
