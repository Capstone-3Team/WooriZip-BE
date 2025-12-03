package org.scoula.backend.domain.archive.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.global.s3.S3Downloader;
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

import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

	private final RestTemplate restTemplate;
	private final S3Downloader s3Downloader;

	@Value("${ai.server.url}")
	private String aiServerUrl;

	// ---------------------------------------------------------
	// 🔥 로컬 파일 경로 분석
	// ---------------------------------------------------------
	public boolean hasPet(String fullPath) {
		try {
			File file = new File(fullPath);
			if (!file.exists()) return false;

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", new FileSystemResource(file));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> request =
				new HttpEntity<>(body, headers);

			ResponseEntity<Map> response =
				restTemplate.postForEntity(aiServerUrl + "/pet_daily", request, Map.class);

			Map res = response.getBody();
			if (res == null) return false;

			Map result = (Map) res.get("result");
			if (result == null) return false;

			return Boolean.TRUE.equals(result.get("is_pet_present"));

		} catch (Exception e) {
			System.out.println("🚨 hasPet() 오류: " + e.getMessage());
			return false;
		}
	}

	// ---------------------------------------------------------
	// 🔥 S3 URL → 다운로드 → 분석
	// ---------------------------------------------------------
	public boolean hasPetFromUrl(String mediaUrl) {
		try {
			// URL → 정확한 S3 key 로 변환
			String key = extractKeyFromUrl(mediaUrl);

			// Key로 다운로드
			File temp = s3Downloader.downloadAsTemp(key);

			boolean result = hasPet(temp.getAbsolutePath());

			temp.delete();
			return result;

		} catch (Exception e) {
			System.out.println("🚨 hasPetFromUrl() 오류: " + e.getMessage());
			return false;
		}
	}

	// ---------------------------------------------------------
	// 🔥 S3 URL → Key 추출기
	// ---------------------------------------------------------
	private String extractKeyFromUrl(String url) {
		try {
			java.net.URL u = new java.net.URL(url);
			String path = u.getPath(); // e.g. /post-images/abc.jpg

			if (path.startsWith("/")) {
				return path.substring(1);
			}
			return path;

		} catch (Exception e) {
			throw new RuntimeException("❌ Invalid S3 URL: " + url);
		}
	}
}
