package org.scoula.backend.domain.mypage.dto;

import lombok.Getter;

@Getter
public class ChangePasswordRequest {
	private String oldPassword;
	private String newPassword;
}
