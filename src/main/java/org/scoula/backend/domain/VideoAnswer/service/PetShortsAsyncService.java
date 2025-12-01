package org.scoula.backend.domain.VideoAnswer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.client.PetShortsAiClient;
import org.scoula.backend.global.s3.S3Downloader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetShortsAsyncService {

	private final PetShortsAiClient petShortsAiClient;
	private final VideoAnswerRepository videoAnswerRepository;
	private final S3Downloader s3Downloader;

	public void processPetShorts(Long answerId) {

		try {
			log.info("🎬 숏츠 처리 시작 answerId={}", answerId);

			VideoAnswer answer = videoAnswerRepository.findById(answerId)
				.orElseThrow(() -> new RuntimeException("VideoAnswer not found: " + answerId));

			answer.setShortsStatus("PROCESSING");
			videoAnswerRepository.save(answer);

			// 1) videoUrl → key 추출
			String key = extractKey(answer.getVideoUrl());

			// 2) S3 파일을 temp로 다운로드
			File videoFile = s3Downloader.downloadAsTemp(key);

			// 3) 반려동물 등장 구간 탐지
			List<List<Double>> segments = petShortsAiClient.detectPetSegments(videoFile);

			// 4) 숏츠 생성
			String shortsUrl = petShortsAiClient.compilePetShorts(
				videoFile.getAbsolutePath(),
				segments
			);

			// 5) DB 업데이트
			answer.setShortsUrl(shortsUrl);
			answer.setShortsStatus("DONE");
			videoAnswerRepository.save(answer);

			log.info("🎉 숏츠 생성 완료 answerId={}", answerId);

		} catch (Exception e) {
			log.error("💥 숏츠 생성 중 오류 발생", e);

			VideoAnswer answer = videoAnswerRepository.findById(answerId).orElse(null);

			if (answer != null) {
				answer.setShortsStatus("FAILED");
				answer.setShortsUrl(null);
				videoAnswerRepository.save(answer);
			}
		}
	}

	// URL → key 변환 (디코딩 포함)
	private String extractKey(String videoUrl) throws Exception {
		URL url = new URL(videoUrl);
		String path = url.getPath().substring(1);
		return URLDecoder.decode(path, StandardCharsets.UTF_8);
	}
}
