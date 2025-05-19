package com.develop.wisebot.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String username;
    private String password;
}
