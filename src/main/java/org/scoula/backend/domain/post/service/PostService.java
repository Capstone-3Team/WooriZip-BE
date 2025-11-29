package org.scoula.backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostMapper postMapper;
	private final FamilyMemberRepository familyMemberRepository;

	public void createPostByEmail(String email, MultipartFile file, String description) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		String mediaUrl = null;

		if (file != null && !file.isEmpty()) {

			String uploadDir = "/Users/juwon/Documents/4-2/Capstone/image/uploads/";

			File directory = new File(uploadDir);
			if (!directory.exists()) directory.mkdirs();

			// 안전한 파일명 처리
			String original = file.getOriginalFilename();
			String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");

			String savePath = uploadDir + safeName;

			try {
				file.transferTo(new File(savePath));
			} catch (Exception e) {
				throw new RuntimeException("파일 저장 실패: " + e.getMessage());
			}

			// DB에 저장되는 경로 (앞에 / 제거)
			mediaUrl = "uploads/" + safeName;
		}

		Post post = Post.builder()
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId())
			.mediaUrl(mediaUrl)
			.description(description)
			.build();

		postMapper.insertPost(post);
	}

	public List<PostResponse> getAllPostsByEmail(String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		return postMapper.findAllPostsByFamilyId(member.getFamilyId());
	}


	public void deletePostByEmail(Long postId, String email) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		postMapper.deletePost(postId, member.getId());
	}

	public void updatePostByEmail(Long postId, String email, String description) {
		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		int updatedRows = postMapper.updatePost(postId, member.getId(), description);

		if (updatedRows == 0) {
			throw new IllegalArgumentException("수정 권한이 없거나 게시글이 존재하지 않습니다.");
		}
	}
}
