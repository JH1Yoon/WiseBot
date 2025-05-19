package com.develop.wisebot.common.message;

import lombok.Getter;

@Getter
public class SuccessResponse {
    private int code;
    private String message;

    public SuccessResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
