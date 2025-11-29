package org.scoula.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageProfileResponse {
	private String profileImage;
	private String nickname;
	private String email;
	private String birth;
	private String phone;
}
