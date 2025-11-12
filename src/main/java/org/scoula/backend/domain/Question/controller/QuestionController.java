package org.scoula.backend.domain.Question.controller;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Question.domain.Question;
import org.scoula.backend.domain.Question.service.QuestionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

	private final QuestionService questionService;

	@GetMapping("/current")
	public Question getCurrentQuestion(@AuthenticationPrincipal User user) {
		String email = user.getUsername(); // JWT에서 추출된 이메일
		return questionService.getCurrentWeekQuestionByEmail(email);
	}


}
