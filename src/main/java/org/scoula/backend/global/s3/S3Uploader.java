package org.scoula.backend.global.s3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class S3Uploader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	// 업로드
	public String upload(MultipartFile file, String folder) {

		try {
			String fileName = folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			metadata.setContentType(file.getContentType());

			amazonS3.putObject(
				new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
			);

			return String.format(
				"https://%s.s3.%s.amazonaws.com/%s",
				bucket,
				region,
				fileName
			);

		} catch (Exception e) {
			throw new RuntimeException("S3 업로드 실패", e);
		}
	}

	// 다운로드 (AWS SDK v1용)
	public byte[] downloadAsBytes(String s3Url) {
		try {
			// 1) key 추출
			String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
			String key = s3Url.replace(prefix, "");

			// 2) 다운로드
			var s3Object = amazonS3.getObject(bucket, key);

			// 3) InputStream → byte[]
			return s3Object.getObjectContent().readAllBytes();

		} catch (Exception e) {
			throw new RuntimeException("S3 다운로드 실패: " + s3Url, e);
		}
	}
}
