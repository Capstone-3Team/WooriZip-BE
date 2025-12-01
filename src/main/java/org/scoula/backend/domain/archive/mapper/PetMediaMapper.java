package org.scoula.backend.domain.archive.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.archive.domain.PetMedia;

import java.util.List;

@Mapper
public interface PetMediaMapper {

	// URL 존재 여부 확인
	PetMedia findByMediaUrl(String mediaUrl);

	// 신규 삽입
	void insertPetMedia(PetMedia media);

	// 반려동물 포함된 것만 조회
	List<PetMedia> findPetMediaByFamilyMemberId(Long familyMemberId);
}
