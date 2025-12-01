package org.scoula.backend.domain.Question.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.Family.domain.Family;
import org.scoula.backend.domain.Family.repository.FamilyRepository;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.Question.domain.Question;
import org.scoula.backend.domain.Question.repository.QuestionRepository;
import org.scoula.backend.global.tts.GoogleTTSService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

	private final FamilyRepository familyRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final QuestionRepository questionRepository;

	public Question getCurrentWeekQuestionByEmail(String email) {
		// 로그인한 유저 → 가족 찾기
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Family family = familyRepository.findById(member.getFamilyId())
			.orElseThrow(() -> new IllegalArgumentException("가족을 찾을 수 없습니다."));

		// 가족 생성일 기준으로 현재 주차 계산
		LocalDate startDate = family.getCreatedAt().toLocalDate();
		long weeks = ChronoUnit.WEEKS.between(startDate, LocalDate.now()) + 1;

		// 해당 주차의 질문 반환
		return questionRepository.findByWeekNumber((int) weeks)
			.orElseThrow(() -> new IllegalArgumentException(weeks + "주차 질문이 존재하지 않습니다."));
	}

	public List<Question> getQuestionList(String email, Integer year, String keyword) {

		// 로그인 유저의 가족 찾기
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Family family = familyRepository.findById(member.getFamilyId())
			.orElseThrow(() -> new IllegalArgumentException("가족을 찾을 수 없습니다."));

		LocalDate familyStart = family.getCreatedAt().toLocalDate();

		// 전체 질문 조회
		List<Question> questions = questionRepository.findAll();

		// 1) 가족 생성년도 이후만 필터
		questions = questions.stream()
			.filter(q -> q.getCreatedAt().getYear() >= familyStart.getYear())
			.collect(Collectors.toList());

		// 2) year 필터
		if (year != null) {
			questions = questions.stream()
				.filter(q -> q.getCreatedAt().getYear() == year)
				.collect(Collectors.toList());
		}

		// 3) keyword 검색
		if (keyword != null && !keyword.isBlank()) {
			final String lower = keyword.toLowerCase();
			questions = questions.stream()
				.filter(q -> q.getTitle().toLowerCase().contains(lower))
				.collect(Collectors.toList());
		}

		return questions;
	}

	private final GoogleTTSService googleTTSService;

	public String getQuestionTTS(Long id) throws Exception {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));

		return googleTTSService.synthesize(question.getTitle());
	}

}
