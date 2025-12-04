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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetShortsAiClient {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String flaskUrl = "http://54.180.94.179:8000";

	// ⭐ 이 메서드가 Map을 반환하도록 반드시 변경!
	public Map<String, Object> detectPetSegments(File videoFile) {

		String url = flaskUrl + "/detect";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("video", new FileSystemResource(videoFile));

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response =
			restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

		Map<String, Object> result = response.getBody();

		// Python에서 output_path 로 올 수 있으므로 호환 처리
		if (result != null && result.containsKey("output_path")) {
			result.put("output", result.get("output_path"));
		}

		return result;
	}

	public String compilePetShorts(String videoPath, List<List<Double>> segments) {

		String url = flaskUrl + "/compile";

		Map<String, Object> payload = new HashMap<>();
		payload.put("video_path", videoPath);
		payload.put("segments", segments);

		ResponseEntity<Map> response = restTemplate.postForEntity(url, payload, Map.class);

		return (String) response.getBody().get("output");
	}
}
