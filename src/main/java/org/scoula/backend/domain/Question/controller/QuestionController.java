package org.scoula.backend.domain.Question.controller;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Question.domain.Question;
import org.scoula.backend.domain.Question.service.QuestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

	private final QuestionService questionService;

	@GetMapping("/current")
	public Question getCurrentQuestion(@RequestParam Integer familyId) {  // ✅ Integer로 변경
		return questionService.getCurrentWeekQuestion(familyId);
	}

}
