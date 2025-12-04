package org.scoula.backend.domain.kakao.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {
	//
	// @Value("${kakao.client-id}")
	// private String clientId;
	//
	// @Value("${kakao.redirect-uri}")
	// private String redirectUri;
	//
	// @Value("${kakao.token-uri}")
	// private String tokenUri;
	//
	// @Value("${kakao.user-info-uri}")
	// private String userInfoUri;
	//
	// private final RestTemplate restTemplate = new RestTemplate();
	//
	// // 1) Access Token 요청
	// public String requestAccessToken(String code) throws Exception {
	//
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	//
	// 	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	// 	params.add("grant_type", "authorization_code");
	// 	params.add("client_id", clientId);
	// 	params.add("redirect_uri", redirectUri);
	// 	params.add("code", code);
	//
	// 	HttpEntity<MultiValueMap<String, String>> request =
	// 		new HttpEntity<>(params, headers);
	//
	// 	ResponseEntity<String> response =
	// 		restTemplate.postForEntity(tokenUri, request, String.class);
	//
	// 	ObjectMapper mapper = new ObjectMapper();
	// 	JsonNode node = mapper.readTree(response.getBody());
	//
	// 	return node.get("access_token").asText();
	// }
	//
	// // 2) 유저 정보 요청
	// public Map<String, Object> getUserInfo(String accessToken) {
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.add("Authorization", "Bearer " + accessToken);
	//
	// 	HttpEntity<?> entity = new HttpEntity<>(headers);
	//
	// 	ResponseEntity<Map> response = restTemplate.exchange(
	// 		userInfoUri,
	// 		HttpMethod.GET,
	// 		entity,
	// 		Map.class
	// 	);
	//
	// 	return response.getBody();
	// }
}
