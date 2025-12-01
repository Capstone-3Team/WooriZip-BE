package org.scoula.backend.domain.VideoAnswer.repository;

import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoAnswerRepository extends JpaRepository<VideoAnswer, Long> {
	List<VideoAnswer> findByQuestionIdAndFamilyId(Long questionId, Long familyId);
	List<VideoAnswer> findByFamilyIdAndShortsStatus(Long familyId, String shortsStatus);
	List<VideoAnswer> findByFamilyId(Long familyId);
	List<VideoAnswer> findByFamilyMemberId(Long familyMemberId);


}
