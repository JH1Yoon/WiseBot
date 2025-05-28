package com.develop.wisebot.domain.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "채팅 응답 DTO")
public class ChatResponse {
    @Schema(description = "질문", example = "스프링 부트란 무엇인가요?")
    private String question;

    @Schema(description = "답변", example = "스프링 부트는 자바 기반의 오픈 소스 프레임워크인 스프링 프레임워크의 일종으로, 스프링 애플리케이션을 빠르고 쉽게 개발할 수 있도록 도와주는 도구입니다. 스프링 부트는 설정이나 구성을 최소화하고, 개발자가 애플리케이션 개발에 집중할 수 있도록 해줍니다. 또한 내장형 서버를 제공하여 애플리케이션을 빠르게 실행하고 배포할 수 있도록 도와줍니다. 스프링 부트는 많은 개발자들에게 사랑받는 프레임워크로, 현대적이고 효율적인 웹 애플리케이션을 개발하는 데 유용하게 사용됩니다.")
    private String answer;

    @Schema(description = "생성 날짜", example = "2025-05-27T09:48:40.686Z")
    private LocalDateTime createdAt;
}