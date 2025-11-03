package org.scoula.backend.domain.FamilyMember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
	private String token;
	private String message;
}
