package com.develop.wisebot.domain.user.dto.response;

import com.develop.wisebot.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResponse {
    private String username;
    private String email;
    private String role;

    public static SearchResponse from(User user) {
        return SearchResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
