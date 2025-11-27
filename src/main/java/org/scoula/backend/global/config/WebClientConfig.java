package org.scoula.backend.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient(WebClient.Builder builder) {

		// 최대 50MB까지 응답 허용
		ExchangeStrategies strategies = ExchangeStrategies.builder()
			.codecs(config -> config
				.defaultCodecs()
				.maxInMemorySize(50 * 1024 * 1024))   // 50MB
			.build();

		return builder
			.exchangeStrategies(strategies)
			.build();
	}
}
