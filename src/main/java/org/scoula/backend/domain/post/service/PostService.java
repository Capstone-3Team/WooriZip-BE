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

	public void createPostByEmail(String email, MultipartFile[] files, String description) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		// 1) Post 먼저 저장
		Post post = Post.builder()
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId())
			.description(description)
			.build();

		postMapper.insertPost(post); // post.id 생성됨

		// 2) 이미지 여러 장 저장
		if (files != null) {
			String uploadDir = "/Users/juwon/Documents/4-2/Capstone/image/uploads/";
			File directory = new File(uploadDir);
			if (!directory.exists()) directory.mkdirs();

			for (MultipartFile file : files) {
				if (file == null || file.isEmpty()) continue;

				String original = file.getOriginalFilename();
				String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
				String savePath = uploadDir + safeName;

				try {
					file.transferTo(new File(savePath));
				} catch (Exception e) {
					throw new RuntimeException("파일 저장 실패: " + e.getMessage());
				}

				String mediaUrl = "uploads/" + safeName;

				// 이미지 1장 저장
				postMapper.insertPostMedia(post.getId(), mediaUrl);
			}
		}
	}


	public List<PostResponse> getAllPostsByEmail(String email) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		List<PostResponse> posts = postMapper.findAllPostsByFamilyId(member.getFamilyId());

		for (PostResponse post : posts) {
			List<String> mediaList = postMapper.findMediaByPostId(post.getId());
			post.setMediaUrls(mediaList);
		}

		return posts;
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
