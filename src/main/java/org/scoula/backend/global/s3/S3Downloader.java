package org.scoula.backend.global.s3;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Downloader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public File downloadAsTemp(String key) throws Exception {

		String decodedKey = URLDecoder.decode(key, StandardCharsets.UTF_8);

		String ext = "";
		int dot = decodedKey.lastIndexOf('.');
		if (dot != -1) ext = decodedKey.substring(dot);

		File temp = File.createTempFile("media_", ext);

		S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, decodedKey));
		InputStream inputStream = s3Object.getObjectContent();

		FileUtils.copyInputStreamToFile(inputStream, temp);

		return temp;
	}

	public String extractKeyFromUrl(String url) {
		int idx = url.indexOf(".amazonaws.com/");
		if (idx == -1) return url;
		return url.substring(idx + ".amazonaws.com/".length());
	}
}
