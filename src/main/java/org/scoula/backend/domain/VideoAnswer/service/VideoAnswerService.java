package org.scoula.backend.domain.VideoAnswer.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswer;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerRequest;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.scoula.backend.global.ai.service.AiAnalysisService;
import org.scoula.backend.global.ai.service.ThumbnailAIService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoAnswerService {
	private final VideoAnswerRepository videoAnswerRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final ThumbnailAIService thumbnailAIService;
	private final AiAnalysisService aiAnalysisService;
	// 업로드
	@Transactional
	public VideoAnswer createVideoAnswer(VideoAnswerRequest request, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 1) 업로드된 videoUrl(현재 로컬 경로)로 File 객체 생성
		File videoFile = new File(request.getVideoUrl());
		if (!videoFile.exists()) {
			throw new IllegalArgumentException("비디오 파일을 찾을 수 없습니다: " + request.getVideoUrl());
		}

		// 2) AI 서버 썸네일 추출
		Map<String, Object> thumbnail = aiAnalysisService.requestThumbnail(videoFile);
		String thumbnailBase64 = (String) thumbnail.get("image_base64");

		// 3) AI 서버 STT + 요약 + 제목 추출
		Map<String, Object> stt = aiAnalysisService.requestStt(videoFile);
		String title = (String) stt.get("title");
		String summary = (String) stt.get("summary");

		// 4) DB 저장
		VideoAnswer answer = VideoAnswer.builder()
			.questionId(request.getQuestionId())
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId().longValue())
			.videoUrl(request.getVideoUrl())
			.thumbnailUrl(thumbnailBase64)
			.title(title)
			.summary(summary)
			.createdAt(LocalDateTime.now())
			.build();

		return videoAnswerRepository.save(answer);
	}


	// 🔹 조회
	public List<VideoAnswer> getAnswers(Long questionId, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Long familyId = member.getFamilyId().longValue();

		// ✅ 로그인한 사용자의 familyId 자동 적용
		return videoAnswerRepository.findByQuestionIdAndFamilyId(questionId, familyId);
	}


	// 🔹 수정
	@Transactional
	public VideoAnswer updateVideoAnswer(Long id, VideoAnswerRequest request, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		// ✅ 본인만 수정 가능
		if (!answer.getFamilyMemberId().equals(member.getId())) {
			throw new SecurityException("본인의 영상만 수정할 수 있습니다.");
		}

		answer.setVideoUrl(request.getVideoUrl());
		answer.setThumbnailUrl(request.getThumbnailUrl());
		return videoAnswerRepository.save(answer);
	}

	// 🔹 삭제
	@Transactional
	public void deleteVideoAnswer(Long id, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		if (!answer.getFamilyMemberId().equals(member.getId())) {
			throw new SecurityException("본인의 영상만 삭제할 수 있습니다.");
		}

		videoAnswerRepository.delete(answer);
	}

	public VideoAnswer getVideoById(Long id, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswer answer = videoAnswerRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		// 같은 가족인지 검증 (optional)
		if (!answer.getFamilyId().equals(member.getFamilyId().longValue())) {
			throw new SecurityException("해당 영상에 접근할 수 없습니다.");
		}

		return answer;
	}


}
