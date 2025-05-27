package com.develop.wisebot.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "회원 탈퇴 요청 DTO")
public class DeleteRequest {
    @Schema(description = "회원 탈퇴를 위한 비밀번호 확인", example = "newpassword123")
    private String password;
}