package org.scoula.backend.domain.VideoAnswer.repository;

import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoAnswerRepository extends JpaRepository<VideoAnswer, Long> {
	List<VideoAnswer> findByQuestionIdAndFamilyId(Long questionId, Long familyId);
	List<VideoAnswer> findByFamilyIdAndShortsStatus(Long familyId, String shortsStatus);
	List<VideoAnswer> findByFamilyId(Long familyId);
	List<VideoAnswer> findByFamilyMemberId(Long familyMemberId);

	// 반려동물 숏츠만 조회
	@Query("SELECT v FROM VideoAnswer v WHERE v.familyId = :familyId AND v.shortsStatus = 'DONE'")
	List<VideoAnswer> findPetShortsByFamilyId(@Param("familyId") Integer familyId);

}
