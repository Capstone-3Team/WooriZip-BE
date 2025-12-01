package org.scoula.backend.domain.archive.service;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
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

			if (responseMap == null) return false;
			if (responseMap.containsKey("error")) return false;

			Map<String, Object> data = (Map<String, Object>) responseMap.getOrDefault("data", responseMap);
			Boolean isPet = (Boolean) data.get("is_pet_present");
			return isPet != null && isPet;

		} catch (Exception e) {
			System.out.println("🚨 AI 서버 오류: " + e.getMessage());
			return false;
		}
	}
}
