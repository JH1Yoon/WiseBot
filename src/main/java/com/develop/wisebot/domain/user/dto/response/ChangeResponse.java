package com.develop.wisebot.domain.user.dto.response;

import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChangeResponse {
    private String username;
    private String email;
    private List<RoleDto> roles;

    @Getter
    @NoArgsConstructor
    public static class RoleDto {
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
