package org.scoula.backend.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
	@Schema(description = "기존 비밀번호", example = "old1234!")
	private String oldPassword;
	@Schema(description = "새 비밀번호", example = "new1234!")
	private String newPassword;
}
