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

	public File downloadAsTemp(String key) throws Exception {

		// key는 반드시 디코딩해야 실제 저장된 key와 일치함
		String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);

		S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, decodedKey));
		InputStream inputStream = s3Object.getObjectContent();

		File temp = File.createTempFile("shorts_", ".mp4");
		FileUtils.copyInputStreamToFile(inputStream, temp);

		return temp;
	}
}
