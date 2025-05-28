package com.develop.wisebot.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "401")
    private int code;

    @Schema(description = "에러 메시지", example = "입력한 키가 관리자 키와 맞지않습니다.")
    private String message;

    @Schema(description = "HTTP 상태명", example = "UNAUTHORIZED")
    private String status;

    public static ErrorResponse from(ErrorCode errorCode) {
        return of(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                errorCode.getStatus().name()
        );
    }
}