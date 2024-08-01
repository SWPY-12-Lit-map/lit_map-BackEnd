package com.lit_map_BackEnd.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 기본 Swagger URI
     * http://localhost:8080/swagger-ui/index.html
     * https://api.litmap.store/swagger-ui/index.html#/
     * https://api.litmap.store
     */

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8080"))
                .addServersItem(new Server().url("https://api.litmap.store"))
                .components(new Components()
                /*    .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")*/
                /*.addSecuritySchemes("bearer-key",
                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")*/

                .addSecuritySchemes("basicAuth", new SecurityScheme()
                        .name("basicAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        ))
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Spring Boot REST API")
                .description("Project Swagger API Documentation")
                .version("1.0.0");
    }

}
