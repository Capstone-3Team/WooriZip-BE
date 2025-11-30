package org.scoula.backend.domain.mypage.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageMainResponse {
	private String profileImage;
	private String nickname;
	private String familyName; // 가족 이름
}
