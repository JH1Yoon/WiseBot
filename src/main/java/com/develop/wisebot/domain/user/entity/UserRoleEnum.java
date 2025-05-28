package com.develop.wisebot.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 역할", example = "ADMIN", allowableValues = {"USER", "ADMIN"})
public enum UserRoleEnum {
    USER(Authority.USER),
    ADMIN(Authority.ADMIN);

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
