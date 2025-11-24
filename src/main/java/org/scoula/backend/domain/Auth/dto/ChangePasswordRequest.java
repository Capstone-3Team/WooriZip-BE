package org.scoula.backend.domain.Auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordRequest {
	private String email;
	private String newPassword;
}
