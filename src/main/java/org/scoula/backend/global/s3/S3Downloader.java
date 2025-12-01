package org.scoula.backend.global.s3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class S3Downloader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	/**
	 * S3 URL(String)에서 객체를 다운로드하여
	 * 원본 확장자를 유지한 temp 파일로 저장해 반환한다.
	 */
	public File downloadAsTemp(String mediaUrl) throws Exception {

		// 1) URL → key 추출
		String key = extractKeyFromUrl(mediaUrl);

		// 2) 디코딩 (공백, 한글, 특수문자 대응)
		String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);

		// 3) 원본 확장자 추출 (.jpg / .png / .mp4 / .mov 등)
		String ext = "";
		int dotIndex = decodedKey.lastIndexOf(".");
		if (dotIndex != -1) {
			ext = decodedKey.substring(dotIndex);  // ".jpg"
		} else {
			ext = ".tmp"; // 혹시 모를 상황 대비
		}

		// 4) 확장자 유지한 temp 파일 생성
		File temp = File.createTempFile("media_", ext);

		// 5) S3에서 파일 다운로드
		S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, decodedKey));
		InputStream inputStream = s3Object.getObjectContent();

		FileUtils.copyInputStreamToFile(inputStream, temp);

		return temp;
	}

	/**
	 * S3 URL 전체에서 key만 추출하는 메서드
	 * 예) https://bucket.s3.region.amazonaws.com/post-images/1234.png
	 *  → post-images/1234.png
	 */
	private String extractKeyFromUrl(String url) {
		String marker = ".amazonaws.com/";
		int idx = url.indexOf(marker);
		if (idx == -1) return url;

		return url.substring(idx + marker.length());
	}
}
