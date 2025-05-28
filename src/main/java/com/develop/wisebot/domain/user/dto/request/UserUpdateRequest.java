package com.develop.wisebot.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원 정보 수정 요청 DTO")
public class UserUpdateRequest {
    @Schema(description = "새로운 사용자 이름", example = "newWiseAdmin")
    private String username;

    @Schema(description = "새로운 비밀번호", example = "newpassword123")
    private String password;
}
