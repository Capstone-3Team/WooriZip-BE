package org.scoula.backend.domain.post.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.post.domain.PostComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostCommentMapper {
	void insertComment(PostComment comment);
	List<PostComment> findCommentsByPostId(Long postId);
	void deleteComment(@Param("id") Long id, @Param("familyMemberId") Long familyMemberId);
}
