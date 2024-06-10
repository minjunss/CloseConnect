package com.CloseConnect.closeconnect.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

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
                .addOpenApiCustomizer(openApi -> {
                    openApi.security(Collections.singletonList(
                            new SecurityRequirement().addList("Authorization")
                    ));
                })
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
