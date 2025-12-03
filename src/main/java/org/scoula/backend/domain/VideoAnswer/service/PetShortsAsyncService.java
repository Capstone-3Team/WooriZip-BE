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
import java.util.Map;

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

			// 3) AI 서버 호출
			Map<String, Object> result = petShortsAiClient.detectPetSegments(videoFile);
			List<List<Double>> segments = (List<List<Double>>) result.get("segments");

			// 4) output or output_path 대응
			String shortsUrl = null;
			if (result.get("output") != null)
				shortsUrl = (String) result.get("output");
			else if (result.get("output_path") != null)
				shortsUrl = (String) result.get("output_path");

			// 실패 처리
			if (shortsUrl == null) {
				answer.setShortsStatus("FAILED");
				videoAnswerRepository.save(answer);
				return;
			}

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

	private String extractKey(String mediaUrl) {
		try {
			// 1) 일반적인 S3 URL 처리
			String marker = ".amazonaws.com/";
			int idx = mediaUrl.indexOf(marker);

			if (idx != -1) {
				return mediaUrl.substring(idx + marker.length());
			}

			// 2) presigned URL 또는 파라미터 포함된 경우 제거
			mediaUrl = mediaUrl.split("\\?")[0];

			// 3) 버킷 없이 key만 들어온 경우
			return mediaUrl;
		} catch (Exception e) {
			log.error("extractKey 실패: {}", mediaUrl);
			return mediaUrl;
		}
	}

}
