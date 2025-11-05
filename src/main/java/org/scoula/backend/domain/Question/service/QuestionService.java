package org.scoula.backend.domain.Question.service;

import lombok.RequiredArgsConstructor;

import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.Question.domain.Question;
import org.scoula.backend.domain.Question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class QuestionService {

	private final FamilyRepository familyRepository;
	private final QuestionRepository questionRepository;

	public Question getCurrentWeekQuestion(Integer familyId) {
		Family family = familyRepository.findById(familyId)
			.orElseThrow(() -> new IllegalArgumentException("가족을 찾을 수 없습니다."));

		LocalDate startDate = family.getCreatedAt().toLocalDate();
		long weeks = ChronoUnit.WEEKS.between(startDate, LocalDate.now()) + 1;

		return questionRepository.findByWeekNumber((int) weeks)
			.orElseThrow(() -> new IllegalArgumentException(weeks + "주차 질문이 존재하지 않습니다."));
	}

}

