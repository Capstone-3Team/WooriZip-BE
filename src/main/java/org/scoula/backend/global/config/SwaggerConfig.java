package org.scoula.backend.global.config;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.servers.Server;
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		// JWT Security 설정
		final String securitySchemeName = "bearerAuth";

		return new OpenAPI()
			.servers(List.of(
				new Server().url("https://woorizip.site")  // 🔥 HTTPS 강제
			))
			.info(new Info()
				.title("WooriZip API")
				.description("""
                                로그인 후 JWT 토큰을 발급받아 Authorize 버튼에 입력하면
                                인증이 필요한 API를 바로 테스트할 수 있습니다.
                                """)
				.version("1.0.0")
			)
			// JWT 설정 추가
			.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
			.components(new Components().addSecuritySchemes(securitySchemeName,
				new SecurityScheme()
					.name(securitySchemeName)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")));

	}


}