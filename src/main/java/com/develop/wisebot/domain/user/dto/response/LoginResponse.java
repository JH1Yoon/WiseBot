package com.develop.wisebot.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {
    @Schema(description = "JWT 인증 토큰", example = "eyJhbGciOiJIUzI1...")
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
