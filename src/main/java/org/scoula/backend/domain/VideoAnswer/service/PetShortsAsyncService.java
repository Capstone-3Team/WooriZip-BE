package org.scoula.backend.domain.VideoAnswer.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.client.PetShortsAiClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetShortsAsyncService {

	private final PetShortsAiClient petShortsAiClient;
	private final VideoAnswerRepository videoAnswerRepository;

	@Transactional
	@Async
	public void processPetShorts(Long answerId) {
		try {
			log.info("반려동물 숏츠 생성 시작 id={}", answerId);

			// 🔥 Detached 엔티티 사용 금지 → DB에서 fresh하게 다시 가져옴
			VideoAnswer answer = videoAnswerRepository.findById(answerId)
				.orElseThrow(() -> new RuntimeException("VideoAnswer not found: " + answerId));

			// 상태 업데이트
			answer.setShortsStatus("PROCESSING");
			videoAnswerRepository.save(answer);

			File videoFile = new File(answer.getVideoUrl());
			if (!videoFile.exists()) {
				throw new RuntimeException("비디오 파일 없음: " + answer.getVideoUrl());
			}

			// 1) 반려동물 등장 구간 분석
			List<List<Double>> segments = petShortsAiClient.detectPetSegments(videoFile);

			// 2) 숏츠 생성
			String outputPath = petShortsAiClient.compilePetShorts(
				videoFile.getAbsolutePath(),
				segments
			);

			// 3) DB 업데이트
			answer.setShortsUrl(outputPath);
			answer.setShortsStatus("DONE");
			videoAnswerRepository.save(answer);

			log.info("반려동물 숏츠 생성 완료 id={}", answerId);

		} catch (Exception e) {
			log.error("숏츠 생성 중 오류", e);

			// fresh하게 다시 조회
			VideoAnswer answer = videoAnswerRepository.findById(answerId)
				.orElse(null);

			if (answer != null) {
				answer.setShortsStatus("FAILED");
				answer.setShortsUrl(null);
				videoAnswerRepository.save(answer);
			}
		}
	}
}
