package org.scoula.backend.global.ai.service;

import java.io.File;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String AI_URL = "http://localhost:8000";

	public Map<String, Object> requestThumbnail(File videoFile) {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("video", new FileSystemResource(videoFile));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		ResponseEntity<Map> response = restTemplate.postForEntity(
			AI_URL + "/thumbnail",
			new HttpEntity<>(body, headers),
			Map.class
		);

		return response.getBody();
	}

	public Map<String, Object> requestStt(File videoFile) {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("video", new FileSystemResource(videoFile));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		ResponseEntity<Map> response = restTemplate.postForEntity(
			AI_URL + "/stt",
			new HttpEntity<>(body, headers),
			Map.class
		);

		return response.getBody();
	}
}
