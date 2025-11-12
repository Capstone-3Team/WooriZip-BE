package org.scoula.backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.post.domain.PostComment;
import org.scoula.backend.domain.post.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommentService {

	private final PostCommentMapper postCommentMapper;

	public void addComment(PostComment comment) {
		postCommentMapper.insertComment(comment);
	}

	public List<PostComment> getComments(Long postId) {
		return postCommentMapper.findCommentsByPostId(postId);
	}

	public void deleteComment(Long commentId, Long familyMemberId) {
		postCommentMapper.deleteComment(commentId, familyMemberId);
	}
}
