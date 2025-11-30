package org.scoula.backend.domain.post.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.post.domain.Post;
import org.apache.ibatis.annotations.Param;
import org.scoula.backend.domain.post.dto.PostResponse;

import java.util.List;

@Mapper
public interface PostMapper {

	void insertPost(Post post);


	int updatePost(@Param("id") Long id,
		@Param("familyMemberId") Long familyMemberId,
		@Param("description") String description);

	void deletePost(@Param("id") Long id, @Param("familyMemberId") Long familyMemberId);



	void insertPostMedia(@Param("postId") Long postId,
		@Param("mediaUrl") String mediaUrl);

	List<PostResponse> findAllPostsByFamilyId(@Param("familyId") Integer familyId);
	List<String> findMediaByPostId(@Param("postId") Long postId);

}