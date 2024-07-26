package com.lit_map_BackEnd.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 기본 Swagger URI
     * http://localhost:8080/swagger-ui/index.html
     */

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    public Info apiInfo() {
        return new Info()
                .title("Spring Boot REST API Test")
                .description("Project Swagger Test")
                .version("1.0.0");
    }

}
