package org.scoula.backend.domain.VideoAnswer.service;

import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.client.PetShortsAiClient;
import org.scoula.backend.global.s3.S3Uploader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetShortsAsyncService {

	private final VideoAnswerRepository videoAnswerRepository;
	private final PetShortsAiClient petShortsAiClient;
	private final S3Uploader s3Uploader;  // 영상 다운로드용

	@Async("AsyncShortsExecutor")
	public void createPetShorts(Long answerId) {

		try {
			log.info("🎬 숏츠 처리 시작 answerId={}", answerId);

			VideoAnswer answer = videoAnswerRepository.findById(answerId)
				.orElseThrow(() -> new IllegalArgumentException("VideoAnswer not found"));

			// 1) S3에서 영상 다운로드
			byte[] videoBytes = s3Uploader.downloadAsBytes(answer.getVideoUrl());

			// 2) Flask /detect 호출
			var detect = petShortsAiClient.detectPetShorts(videoBytes);

			// 3) DB 업데이트
			answer.setShortsStatus("DONE");
			answer.setShortsUrl(detect.getShorts_url());
			answer.setThumbnailUrl(detect.getThumbnail_url());
			answer.setSummary(detect.getSummary());

			videoAnswerRepository.save(answer);

			log.info("✅ 숏츠 생성 완료! answerId={}", answerId);

		} catch (Exception e) {
			log.error("💥 숏츠 생성 중 오류 발생", e);

			videoAnswerRepository.findById(answerId).ifPresent(a -> {
				a.setShortsStatus("ERROR");
				videoAnswerRepository.save(a);
			});
		}
	}
}
