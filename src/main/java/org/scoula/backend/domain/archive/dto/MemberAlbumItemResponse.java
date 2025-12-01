package org.scoula.backend.domain.archive.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberAlbumItemResponse {

	private String type;             // VIDEO_ANSWER | IMAGE | VIDEO
	private String url;
	private LocalDateTime createdAt;
	private String profileImageUrl;
	private String nickname;         // ⭐ 업로더 별명 추가
}
