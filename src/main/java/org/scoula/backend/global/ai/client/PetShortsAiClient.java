package org.scoula.backend.global.ai.client;

import org.scoula.backend.global.ai.dto.PetDetectResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PetShortsAiClient {

	private final RestTemplate restTemplate;

	private static final String AI_SERVER = "http://localhost:8000";

	public PetDetectResponse detectPetShorts(byte[] videoBytes) {

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		var body = new LinkedMultiValueMap<String, Object>();
		body.add("video", new ByteArrayResource(videoBytes) {
			@Override
			public String getFilename() {
				return "video.mp4";
			}
		});

		var entity = new HttpEntity<>(body, headers);

		ResponseEntity<PetDetectResponse> response = restTemplate.exchange(
			AI_SERVER + "/detect",
			HttpMethod.POST,
			entity,
			PetDetectResponse.class
		);

		return response.getBody();
	}
}
