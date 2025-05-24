package com.develop.wisebot.domain.guide.dto.response;

import lombok.Getter;

@Getter
public class GuideResponse {
    private String guide;

    public GuideResponse(String guide){
        this.guide = guide;
    }
}
