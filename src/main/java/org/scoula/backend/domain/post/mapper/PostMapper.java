package org.scoula.backend.domain.post.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.scoula.backend.domain.post.domain.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
	void insertPost(Post post);
	List<Post> findAllPostsByFamilyId(@Param("familyId") Integer familyId);
	void deletePost(@Param("id") Long id, @Param("familyMemberId") Long familyMemberId);
}