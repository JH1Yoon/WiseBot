package com.develop.wisebot.common.message;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessMessage {

    // Chat
    CHAT_DELETED(HttpStatus.OK, "%s의 %s 채팅을 삭제했습니다."),

    // 기본 코드
    POSTED(HttpStatus.CREATED, "%s을(를) 등록했습니다."),
    CREATED(HttpStatus.CREATED, "%s을(를) 생성했습니다."),
    MODIFIED(HttpStatus.OK, "%s을(를) 수정했습니다."),
    DELETED(HttpStatus.OK, "%s을(를) 삭제했습니다.");

    private final HttpStatus status;
    private final String message;

    SuccessMessage(HttpStatus httpStatus, String message){
        this.status = httpStatus;
        this.message = message;
    }

    public String getMessage(String detail) {
        return String.format(message, detail);
    }

    public String getMessage(String detail1, String detail2) {
        return String.format(message, detail1, detail2);
    }
}