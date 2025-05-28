package com.develop.wisebot.domain.user.dto.response;

import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "회원 정보 수정 응답 DTO")
public class ChangeResponse {
    @Schema(description = "변경된 사용자 이름", example = "newWiseAdmin")
    private String username;

    @Schema(description = "변경된 이메일", example = "wiseadmin@example.com")
    private String email;

    @Schema(description = "현재 역할 목록")
    private List<RoleDto> roles;

    @Getter
    @NoArgsConstructor
    public static class RoleDto {
        @Schema(description = "변경 후 역할", example = "ADMIN")
        private UserRoleEnum role;

        public RoleDto(UserRoleEnum role) {
            this.role = role;
        }
    }

    public ChangeResponse(String username, String email, List<RoleDto> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
