package org.scoula.backend.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.scoula.backend.domain.FamilyMember.domain.FamilyMember;
import org.scoula.backend.domain.FamilyMember.repository.FamilyMemberRepository;
import org.scoula.backend.domain.archive.domain.PetMedia;
import org.scoula.backend.domain.archive.mapper.PetMediaMapper;
import org.scoula.backend.domain.archive.service.AIService;
import org.scoula.backend.domain.post.domain.Post;
import org.scoula.backend.domain.post.dto.PostResponse;
import org.scoula.backend.domain.post.mapper.PostMapper;
import org.scoula.backend.global.s3.S3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostMapper postMapper;
	private final FamilyMemberRepository familyMemberRepository;

	private final S3Uploader s3Uploader;
	private final AIService aiService;
	private final PetMediaMapper petMediaMapper;




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

	public List<String> getAllPostImages() {
		return postMapper.findAllPostImages();
	}

	public void createPostByEmail(String email, MultipartFile[] files, String description) {

		FamilyMember member = familyMemberRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

		Post post = Post.builder()
			.familyMemberId(member.getId())
			.familyId(member.getFamilyId())
			.description(description)
			.build();

		postMapper.insertPost(post);

		if (files != null) {
			for (MultipartFile file : files) {
				if (file == null || file.isEmpty()) continue;

				String s3Url = s3Uploader.upload(file, "post-images");
				postMapper.insertPostMedia(post.getId(), s3Url);

				// 🔥 반려동물 감지 추가
				boolean isPet = aiService.hasPetFromUrl(s3Url);

				PetMedia media = new PetMedia();
				media.setMediaUrl(s3Url);
				media.setPostId(post.getId());
				media.setFamilyMemberId(member.getId());
				media.setIsPet(isPet);

				petMediaMapper.insertPetMedia(media);
			}
		}
	}
}
