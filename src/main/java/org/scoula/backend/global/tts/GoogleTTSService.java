package org.scoula.backend.global.tts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Base64;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.protobuf.ByteString;

@Service
@RequiredArgsConstructor
public class GoogleTTSService {


	@Value("${google.tts.key-path}")
	private Resource keyPath;

	public String synthesize(String text) throws Exception {

		TextToSpeechSettings settings =
			TextToSpeechSettings.newBuilder()
				.setCredentialsProvider(() ->
					GoogleCredentials.fromStream(keyPath.getInputStream()))
				.build();

		try (TextToSpeechClient client = TextToSpeechClient.create(settings)) {

			SynthesisInput input = SynthesisInput.newBuilder()
				.setText(text)
				.build();

			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
				.setLanguageCode("ko-KR")
				.setSsmlGender(SsmlVoiceGender.FEMALE)
				.build();

			AudioConfig audioConfig = AudioConfig.newBuilder()
				.setAudioEncoding(AudioEncoding.MP3)
				.build();

			SynthesizeSpeechResponse response =
				client.synthesizeSpeech(input, voice, audioConfig);

			ByteString audioContents = response.getAudioContent();
			return Base64.getEncoder().encodeToString(audioContents.toByteArray());
		}
	}
}