package org.scoula.backend.domain.Question.controller;

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


}
