package com.develop.wisebot.domain.user.dto.response;

import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class SignupResponse {
    @Schema(description = "회원의 사용자 이름", example = "wiseadmin")
    private String username;

    @Schema(description = "회원의 이메일", example = "wiseadmin@example.com")
    private String email;

    @Schema(description = "회원의 역할 목록")
    private List<RoleDto> roles;

    @Getter
    @NoArgsConstructor
    public static class RoleDto {
        @Schema(description = "회원의 역할", example = "ADMIN")
        private UserRoleEnum role;

        public RoleDto(UserRoleEnum role) {
            this.role = role;
        }
    }

    public SignupResponse(String username, String email, List<RoleDto> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}