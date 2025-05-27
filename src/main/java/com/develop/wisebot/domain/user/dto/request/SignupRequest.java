package com.develop.wisebot.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "회원가입 요청 DTO")
public class SignupRequest {
    @Schema(description = "사용자 이름", example = "wiseadmin")
    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank(message = "password는 필수입니다.")
    private String password;

    @Schema(description = "이메일 주소", example = "wiseadmin@example.com")
    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}