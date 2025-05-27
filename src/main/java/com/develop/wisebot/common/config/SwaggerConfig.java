package com.develop.wisebot.common.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WiseBot API 문서")
                        .version("1.0.0")
                        .description("AI 기반 Q&A 챗봇 시스템 API 문서")
                        .contact(new Contact()
                                .name("WiseBot")
                                .email("wlgus4589@naver.com")
                                .url("https://github.com/JH1Yoon/WiseBot")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}