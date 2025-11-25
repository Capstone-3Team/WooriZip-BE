package org.scoula.backend.global.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Slf4j
@Service
public class ThumbnailAIService {
	private final String AI_SERVER_URL = "http://localhost:8000/analyze"; // Flask 서버 URL
	public String getThumbnailBase64(File videoFile) {
		RestTemplate restTemplate = new RestTemplate();
		// 요청 Body (Multipart)
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("video", new FileSystemResource(videoFile));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity =
			new HttpEntity<>(body, headers);
		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(
				AI_SERVER_URL,
				requestEntity,
				Map.class
			);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new RuntimeException("AI 서버가 정상 응답하지 않음");
			}
			return (String) response.getBody().get("image_base64");
		} catch (Exception e) {
			log.error("AI 썸네일 분석 실패", e);
			throw new RuntimeException("AI 썸네일 분석 실패: " + e.getMessage());
		}
	}
}
