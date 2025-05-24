package com.develop.wisebot.domain.openai.service;

import com.develop.wisebot.domain.openai.dto.request.OpenAiRequest;
import com.develop.wisebot.domain.openai.dto.response.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient webClient;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    public String askToGpt(String question) {
        OpenAiRequest request = OpenAiRequest.of(model, question);

        OpenAiResponse response = webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .block();

        if (response == null || response.getChoices().isEmpty()) {
            return "GPT 응답이 없습니다.";
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}