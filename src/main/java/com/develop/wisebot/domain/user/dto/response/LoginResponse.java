package com.develop.wisebot.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
