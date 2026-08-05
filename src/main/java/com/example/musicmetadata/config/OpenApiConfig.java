package com.example.musicmetadata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI musicMetadataOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Music Metadata API")
                        .version("v1")
                        .description("Manage artist and track metadata and retrieve the rotating Artist of the Day.")
                        .contact(new Contact().name("Music Metadata Service"))
                        .license(new License().name("Proprietary")))
                .servers(List.of(new Server().url("/").description("Current server")));
    }
}
