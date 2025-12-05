package org.scoula.backend.global.s3;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3Uploader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region.static}")
	private String region;

	public String upload(MultipartFile file, String folder) {

		try {
			String fileName = folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(file.getSize());
			metadata.setContentType(file.getContentType());

			amazonS3.putObject(
				new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
			);

			// 리전 포함 URL 직접 구성
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

	public void delete(String imageUrl) {
		try {
			// 파일명만 추출
			String key = imageUrl.substring(imageUrl.indexOf("post-images"));
			amazonS3.deleteObject(bucket, key);
		} catch (Exception e) {
			throw new RuntimeException("S3 파일 삭제 실패: " + imageUrl, e);
		}
	}

}
