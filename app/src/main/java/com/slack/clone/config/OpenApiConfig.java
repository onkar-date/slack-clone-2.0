package com.slack.clone.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI slackCloneOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Slack Clone API")
                        .description("REST API for Slack-like chat application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Slack Clone Team")
                                .email("support@slackclone.com")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authentication")));
    }
}
