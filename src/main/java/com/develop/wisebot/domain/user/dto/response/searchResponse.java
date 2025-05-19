package com.develop.wisebot.domain.user.dto.response;

import com.develop.wisebot.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class searchResponse {
    private String username;
    private String email;
    private String role;

    public static searchResponse from(User user) {
        return searchResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
