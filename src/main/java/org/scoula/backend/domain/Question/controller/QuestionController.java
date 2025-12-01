package org.scoula.backend.domain.Question.controller;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Question.domain.Question;
import org.scoula.backend.domain.Question.service.QuestionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
@Tag(name = "Question", description = "질문 API")
public class QuestionController {

	private final QuestionService questionService;

	@GetMapping("/current")
	@Operation(
		summary = "이번주 질문 조회",
		description = "로그인한 사용자의 이메일(JWT 기반)을 바탕으로 현재 주차의 질문을 조회합니다."
	)
	public Question getCurrentQuestion(@AuthenticationPrincipal User user) {
		String email = user.getUsername(); // JWT에서 추출된 이메일
		return questionService.getCurrentWeekQuestionByEmail(email);
	}

	@GetMapping("/list")
	@Operation(
		summary = "주차별 질문 전체 조회",
		description = "년도 기준으로 모든 주차 질문 목록을 조회하며, 검색 키워드로 필터링할 수 있습니다."
	)
	public List<Question> getQuestionList(
		@RequestParam(required = false) Integer year,
		@RequestParam(required = false) String keyword,
		@AuthenticationPrincipal User user
	) {
		String email = user.getUsername();
		return questionService.getQuestionList(email, year, keyword);
	}
	@GetMapping("/tts/{id}")
	@Operation(summary = "질문 읽기(TTS)", description = "질문 내용을 Google Cloud TTS로 음성 변환합니다.")
	public Map<String, Object> getQuestionTTS(@PathVariable Long id) throws Exception {

		String audioBase64 = questionService.getQuestionTTS(id);

		return Map.of(
			"audio", audioBase64,
			"format", "mp3"
		);
	}




}
