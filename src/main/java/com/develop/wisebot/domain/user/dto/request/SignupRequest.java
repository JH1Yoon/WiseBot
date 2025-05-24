package com.develop.wisebot.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {
    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @NotBlank(message = "password는 필수입니다.")
    private String password;

    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;
}