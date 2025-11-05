package org.scoula.backend.domain.VideoAnswer.service;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoAnswerService {

	private final VideoAnswerRepository videoAnswerRepository;

	// 🔹 업로드
	public VideoAnswer createVideoAnswer(VideoAnswerRequest request) {
		VideoAnswer answer = VideoAnswer.builder()
			.questionId(request.getQuestionId())
			.familyMemberId(request.getFamilyMemberId())
			.familyId(request.getFamilyId())
			.videoUrl(request.getVideoUrl())
			.thumbnailUrl(request.getThumbnailUrl())
			.createdAt(LocalDateTime.now())
			.build();
		return videoAnswerRepository.save(answer);
	}

	// 🔹 조회
	public List<VideoAnswer> getAnswers(Long questionId, Long familyId) {
		return videoAnswerRepository.findByQuestionIdAndFamilyId(questionId, familyId);
	}

	// 🔹 수정
	public VideoAnswer updateVideoAnswer(Long id, VideoAnswerRequest request) {
		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		answer.setVideoUrl(request.getVideoUrl());
		answer.setThumbnailUrl(request.getThumbnailUrl());
		return videoAnswerRepository.save(answer);
	}

	// 🔹 삭제
	public void deleteVideoAnswer(Long id) {
		videoAnswerRepository.deleteById(id);
	}
}
