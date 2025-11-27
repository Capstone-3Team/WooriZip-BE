package org.scoula.backend.global.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisService {

	private final WebClient webClient;
	private static final String AI_URL = "http://localhost:8000";

	//공통적으로 사용할 multipart body 생성
	private MultiValueMap<String, Object> createMultipart(File videoFile) {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("video", new FileSystemResource(videoFile));
		return body;
	}

	// 🎯 썸네일 요청
	public Map<String, Object> requestThumbnail(File videoFile) {

		MultiValueMap<String, Object> body = createMultipart(videoFile);

		return webClient.post()
			.uri(AI_URL + "/thumbnail")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.retrieve()
			.bodyToMono(Map.class)
			.doOnError(e -> log.error("썸네일 AI 호출 오류", e))
			.block();   // sync
	}

	// 🎯 STT + 요약 + 제목 요청
	public Map<String, Object> requestStt(File videoFile) {

		MultiValueMap<String, Object> body = createMultipart(videoFile);

		return webClient.post()
			.uri(AI_URL + "/stt")
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.body(BodyInserters.fromMultipartData(body))
			.retrieve()
			.bodyToMono(Map.class)
			.doOnError(e -> log.error("STT AI 호출 오류", e))
			.block();   // sync
	}
}
