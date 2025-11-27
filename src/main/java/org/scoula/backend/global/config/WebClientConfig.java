package org.scoula.backend.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		// 10MB까지 응답 허용
		ExchangeStrategies strategies = ExchangeStrategies.builder()
			.codecs(config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
			.build();

		return builder
			.exchangeStrategies(strategies)
			.build();
	}
}
