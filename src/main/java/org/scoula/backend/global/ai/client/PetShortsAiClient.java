package org.scoula.backend.global.ai.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetShortsAiClient {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String flaskUrl = "http://localhost:8000"; // 필요시 변경

	// 1) 반려동물 구간 감지
	public List<List<Double>> detectPetSegments(File videoFile) {

		String url = flaskUrl + "/detect";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("video", new FileSystemResource(videoFile));

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

		return (List<List<Double>>) response.getBody().get("segments");
	}

	// 2) 숏츠 생성 요청
	public String compilePetShorts(String videoPath, List<List<Double>> segments) {

		String url = flaskUrl + "/compile";

		Map<String, Object> payload = new HashMap<>();
		payload.put("video_path", videoPath);
		payload.put("segments", segments);

		ResponseEntity<Map> response = restTemplate.postForEntity(url, payload, Map.class);

		return (String) response.getBody().get("output"); // output_path
	}
}
