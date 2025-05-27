package com.develop.wisebot.domain.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "채팅 요청 DTO")
public class ChatRequest {
    @Schema(description = "질문", example = "스프링 부트란 무엇인가요?")
    private String question;
}