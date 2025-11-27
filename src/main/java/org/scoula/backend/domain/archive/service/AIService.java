package org.scoula.backend.domain.archive.service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

	private final RestTemplate restTemplate;

	@Value("${ai.server.url}")
	private String aiServerUrl;

	public boolean hasPet(String fullPath) {

		try {
			File checkFile = new File(fullPath);
			System.out.println("🔍 AI로 보내는 파일: " + fullPath);
			System.out.println("📁 파일 존재?: " + checkFile.exists());

			if (!checkFile.exists()) {
				System.out.println("❌ 파일이 실제로 존재하지 않습니다.");
				return false;
			}

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", new FileSystemResource(fullPath));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> request =
				new HttpEntity<>(body, headers);

			ResponseEntity<Map> response =
				restTemplate.postForEntity(aiServerUrl + "/classify", request, Map.class);

			Map<String, Object> responseMap = response.getBody();

			if (responseMap == null) {
				System.out.println("❌ AI 응답 null");
				return false;
			}

			// Flask 오류 메시지 처리
			if (responseMap.containsKey("error")) {
				System.out.println("⚠️ AI 서버 오류: " + responseMap.get("error"));
				return false;
			}

			Map<String, Object> data;

			if (responseMap.containsKey("data")) {
				data = (Map<String, Object>) responseMap.get("data");
			} else {
				data = responseMap;
			}

			if (data == null || data.get("is_pet_present") == null) {
				System.out.println("⚠️ AI 응답 오류: " + responseMap);
				return false;
			}

			Boolean isPet = (Boolean) data.get("is_pet_present");
			return isPet != null && isPet;

		} catch (Exception e) {
			System.out.println("🚨 AI 서버 오류: " + e.getMessage());
			return false;
		}
	}
}
