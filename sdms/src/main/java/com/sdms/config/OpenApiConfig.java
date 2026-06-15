package com.sdms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger (now commonly called OpenAPI) is used to document, visualize, and test REST APIs.
//http://localhost:8080/swagger-ui/index.html  to test your APIs in a user-friendly interface. It shows all available endpoints, their required inputs, and expected outputs. You can also execute API calls directly from the Swagger UI without needing Postman.
// For your Secure Document Management System, Swagger provides a web page where developers can see all available APIs, their inputs, outputs, and try them without using Postman.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Secure Document Management API")
                        .version("1.0")
                        .description("JWT secured Document Management System"))
                .components(new Components()
                        .addSecuritySchemes(
                                "Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("Bearer Authentication")
                );
    }
}