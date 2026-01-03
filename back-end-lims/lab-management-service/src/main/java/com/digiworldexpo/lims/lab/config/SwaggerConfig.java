package com.digiworldexpo.lims.lab.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

	@Value("${server.port}")
	private String serverPort;

	@Value("${server.servlet.context-path}")
	private String context;

	@Bean
	public OpenAPI customOpenAPI() {
		String url = "http://localhost:" + serverPort + context;

		return new OpenAPI()
				.info(new Info().title("Lab Management API")
						.description("API documentation for Lab Management Service with JWT Authentication"))
				.addSecurityItem(new SecurityRequirement().addList("Authorization"))
				.components(new Components().addSecuritySchemes("Authorization", createAPIKeyScheme()))
				.servers(List.of(new Server().url(url).description("Local Server")));
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().name("Authorization").scheme("bearer").bearerFormat("JWT")
				.type(SecurityScheme.Type.HTTP);
	}
}
