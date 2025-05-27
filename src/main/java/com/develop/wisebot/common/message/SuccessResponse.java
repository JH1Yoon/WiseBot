package com.develop.wisebot.common.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "성공 응답")
public class SuccessResponse {
    @Schema(description = "HTTP 상태 코드", example = "200")
    private int code;

    @Schema(description = "성공 메시지", example = "회원 탈퇴가 완료되었습니다.")
    private String message;

    public SuccessResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
