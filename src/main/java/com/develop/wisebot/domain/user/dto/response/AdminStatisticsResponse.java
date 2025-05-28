package com.develop.wisebot.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "관리자 통계 항목 조회 요청 DTO")
public class AdminStatisticsResponse {
    @Schema(description = "총 질문 수", example = "1")
    private long totalChats;

    @Schema(description = "총 사용자 수", example = "1")
    private long totalUsers;

    @Schema(description = "오늘 생성된 질문 수", example = "0")
    private long todayChats;

    @Schema(description = "오늘 가입한 사용자 수", example = "1")
    private long todayUsers;

    @Schema(description = "최근 질문 목록 (최신순 5개)")
    private List<ChatSummary> recentChats;

    @Getter
    @Builder
    public static class ChatSummary {
        @Schema(description = "질문 내용", example = "스프링 부트란 무엇인가요?")
        private String question;

        @Schema(description = "답변 내용", example = "스프링 부트(Spring Boot)는 자바 웹 애플리케이션을 빠르고 쉽게 개발할 수 있도록 도와주는 프레임워크입니다. 스프링 부트는 스프링 프레임워크를 기반으로 한 마이크로서비스 및 웹 애플리케이션을 쉽게 구축할 수 있도록 많은 기능을 제공합니다.\\n\\n스프링 부트는 자동 구성(auto-configuration) 기능을 통해 개발자가 별도의 설정을 하지 않아도 기본적인 설정을 자동으로 처리해줍니다. 또한 내장형 서버(embedded server)를 제공하여 애플리케이션을 쉽게 실행하고 배포할 수 있도록 도와줍니다.\\n\\n스프링 부트는 스프링 프레임워크와 다양한 외부 라이브러리와의 통합을 쉽게 할 수 있도록 지원하며, 테스트, 보안, 모니터링 등 다양한 기능을 제공합니다. 이러한 기능들을 통해 개발자들은 빠르고 효율적으로 안정적인 웹 애플리케이션을 개발할 수 있습니다.")
        private String answer;

        @Schema(description = "질문 생성 시각", example = "2025-05-27T10:30:00")
        private LocalDateTime createdAt;
    }

}
