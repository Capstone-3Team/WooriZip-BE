package org.scoula.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FamilyProfileResponse {

	private String familyName;
	private String inviteCode;


	private MemberInfo leader;
	private List<MemberInfo> members;

	@Getter
	@AllArgsConstructor
	public static class MemberInfo {
		private String nickname;
		private String profileImage;
	}
}
