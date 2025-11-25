package org.scoula.backend.domain.post.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.post.domain.PostComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostCommentMapper {
	void insertComment(PostComment comment);
	List<PostComment> findCommentsByPostId(@Param("postId") Long postId);
	List<PostComment> findAllComments();
	int updateComment(
		@Param("id") Long id,
		@Param("familyMemberId") Long familyMemberId,
		@Param("content") String content
	);
	int deleteComment(@Param("id") Long id, @Param("familyMemberId") Long familyMemberId);
}
