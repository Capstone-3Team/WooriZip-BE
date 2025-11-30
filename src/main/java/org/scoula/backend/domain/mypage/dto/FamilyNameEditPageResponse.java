package org.scoula.backend.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FamilyNameEditPageResponse {
	private String familyName;
	private String lastModifiedBy;  // 마지막 수정자 (nullable 가능)
}
