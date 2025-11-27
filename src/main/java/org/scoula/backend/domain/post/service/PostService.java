package org.scoula.backend.domain.post.service;


import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostMapper postMapper;
	private final FamilyMemberRepository familyMemberRepository;
	// 이메일로 게시글 생성
	public void createPostByEmail(String email, MultipartFile file, String description) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		String mediaUrl = (file != null && !file.isEmpty())
			? "/uploads/" + file.getOriginalFilename()
			: null;
		Post post = Post.builder()
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId())
			.mediaUrl(mediaUrl)
			.description(description)
			.build();
		postMapper.insertPost(post);
	}
	// 이메일로 해당 가족의 게시글 조회
	public List<Post> getAllPostsByEmail(String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		return postMapper.findAllPostsByFamilyId(member.getFamilyId());
	}
	public void deletePostByEmail(Long postId, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		postMapper.deletePost(postId, member.getId());
	}

	// 게시글 수정 (설명만)
	public void updatePostByEmail(Long postId, String email, String description) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 수정 쿼리 실행
		int updatedRows = postMapper.updatePost(postId, member.getId(), description);

		if (updatedRows == 0) {
			throw new IllegalArgumentException("수정 권한이 없거나 게시글이 존재하지 않습니다.");
		}
	}

}
