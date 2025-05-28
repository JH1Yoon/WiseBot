package com.develop.wisebot.domain.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;


@Getter
@AllArgsConstructor
public class OpenAiRequest {

    private String model;

    private List<Message> messages;

    private double temperature = 0.7;

    @JsonProperty("max_tokens")
    private int maxTokens = 1000;

    public static OpenAiRequest of(String model, String question) {
        Message message = new Message("user", question);
        return new OpenAiRequest(model, Collections.singletonList(message), 0.7, 1000);
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}