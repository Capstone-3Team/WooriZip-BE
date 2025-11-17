package org.scoula.backend.domain.VideoAnswer.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.VideoAnswer.domain.VideoAnswerComment;
import org.scoula.backend.domain.VideoAnswer.dto.VideoAnswerCommentRequest;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerCommentRepository;
import org.scoula.backend.domain.VideoAnswer.repository.VideoAnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoAnswerCommentService {

	private final VideoAnswerCommentRepository commentRepository;
	private final FamilyMemberRepository familyMemberRepository;
	private final VideoAnswerRepository videoAnswerRepository;

	// 🔹 댓글 생성
	@Transactional
	public VideoAnswerComment createComment(VideoAnswerCommentRequest request, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 영상 존재 여부 확인
		videoAnswerRepository.findById(request.getVideoAnswerId())
			.orElseThrow(() -> new IllegalArgumentException("영상 답변을 찾을 수 없습니다."));

		VideoAnswerComment comment = VideoAnswerComment.builder()
			.videoAnswerId(request.getVideoAnswerId())
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId().longValue())
			.content(request.getContent())
			.createdAt(LocalDateTime.now())
			.build();

		return commentRepository.save(comment);
	}

	// 🔹 댓글 조회
	public List<VideoAnswerComment> getComments(Long videoAnswerId) {
		return commentRepository.findByVideoAnswerId(videoAnswerId);
	}

	// 🔹 댓글 수정
	@Transactional
	public VideoAnswerComment updateComment(Long id, VideoAnswerCommentRequest request, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswerComment comment = commentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

		if (!comment.getFamilyMemberId().equals(member.getId())) {
			throw new SecurityException("본인의 댓글만 수정할 수 있습니다.");
		}

		comment.setContent(request.getContent());
		return commentRepository.save(comment);
	}

	// 🔹 댓글 삭제
	@Transactional
	public void deleteComment(Long id, String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		VideoAnswerComment comment = commentRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

		if (!comment.getFamilyMemberId().equals(member.getId())) {
			throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
		}

		commentRepository.delete(comment);
	}
}
