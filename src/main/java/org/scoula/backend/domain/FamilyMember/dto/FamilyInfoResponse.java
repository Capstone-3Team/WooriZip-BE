package org.scoula.backend.domain.FamilyMember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FamilyInfoResponse {
	private String familyName;        // 가족 별명
	private Long leaderId;            // 대표 ID
	private String leaderNickname;    // 대표 닉네임
	private String leaderProfile;     // 대표 프로필 이미지
}
