package org.scoula.backend.domain.archive.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ArchiveMainResponse {

	private List<AlbumItemResponse> daily;
	private List<AlbumItemResponse> member;
	private List<AlbumItemResponse> pet;
}
