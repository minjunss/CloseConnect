package com.CloseConnect.closeconnect.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@OpenAPIDefinition(
        info = @Info(title = "CloseConnect",
        description = "CloseConnect API Docs",
        version = "v1")
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"/api/**"};
        return GroupedOpenApi.builder()
                .group("CloseConnect API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi loginApi() {
        String[] paths = {"/oauth2/**"};
        return GroupedOpenApi.builder()
                .group("OAuth 2.0 API")
                .pathsToMatch(paths)
                .build();
    }

}
