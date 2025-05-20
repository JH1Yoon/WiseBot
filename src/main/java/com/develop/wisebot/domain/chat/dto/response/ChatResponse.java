package com.develop.wisebot.domain.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponse {
    private String question;
    private String answer;
    private LocalDateTime createdAt;
}
