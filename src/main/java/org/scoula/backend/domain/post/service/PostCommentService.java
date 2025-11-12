package org.scoula.backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.post.domain.PostComment;
import org.scoula.backend.domain.post.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {

	private final PostCommentMapper postCommentMapper;
	private final FamilyMemberRepository familyMemberRepository;

	// ✅ 댓글 등록
	public void createComment(String email, Long postId, String content) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		PostComment comment = PostComment.builder()
			.postId(postId)
			.familyMemberId(member.getId())
			.content(content)
			.build();

		postCommentMapper.insertComment(comment);
	}

	// ✅ 특정 게시글 댓글 조회
	public List<PostComment> getCommentsByPostId(Long postId) {
		return postCommentMapper.findCommentsByPostId(postId);
	}

	// ✅ 전체 댓글 조회
	public List<PostComment> getAllComments() {
		return postCommentMapper.findAllComments();
	}

	// ✅ 댓글 수정
	public void updateComment(Long commentId, String email, String content) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		int updated = postCommentMapper.updateComment(commentId, member.getId(), content);
		if (updated == 0) {
			throw new IllegalStateException("수정할 댓글이 존재하지 않거나 권한이 없습니다.");
		}
	}

	// ✅ 댓글 삭제 (본인만 가능)
	public void deleteCommentByEmail(Long commentId, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		int deleted = postCommentMapper.deleteComment(commentId, member.getId());
		if (deleted == 0) {
			throw new IllegalStateException("삭제할 댓글이 존재하지 않거나 권한이 없습니다.");
		}
	}
}
