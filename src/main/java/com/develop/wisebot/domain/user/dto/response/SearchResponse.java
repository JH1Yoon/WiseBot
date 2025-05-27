package com.develop.wisebot.domain.user.dto.response;

import com.develop.wisebot.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "가입한 모든 회원 조회 응답 DTO")
public class SearchResponse {
    @Schema(description = "사용자 이름", example = "wiseadmin")
    private String username;

    @Schema(description = "사용자 이메일", example = "wiseadmin@example.com")
    private String email;

    @Schema(description = "사용자 역할", example = "ADMIN")
    private String role;

    public static SearchResponse from(User user) {
        return SearchResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
