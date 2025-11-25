package org.scoula.backend.domain.VideoAnswer.repository;

import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswerComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoAnswerCommentRepository extends JpaRepository<VideoAnswerComment, Long> {
	List<VideoAnswerComment> findByVideoAnswerId(Long videoAnswerId);
}
