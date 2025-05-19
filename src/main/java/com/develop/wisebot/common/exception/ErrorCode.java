package com.develop.wisebot.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // User
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "%s는 이미 존재합니다."),
    INVALID_ADMIN_KEY(HttpStatus.UNAUTHORIZED, "입력한 키가 관리자 키와 맞지않습니다."),

    // 기본 코드
    NOT_FOUND(HttpStatus.NOT_FOUND, "%s을(를) 찾지못했습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message){
        this.status = httpStatus;
        this.message = message;
    }
}