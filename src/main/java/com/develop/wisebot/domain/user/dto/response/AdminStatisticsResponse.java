package com.develop.wisebot.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminStatisticsResponse {
    private long totalChats;    // 총 질문 수
    private long totalUsers;    // 총 사용자 수
    private long todayChats;    // 오늘 생성된 질문 수
    private long todayUsers;    // 오늘 가입한 사용자 수
    private List<ChatSummary> recentChats;      // 최근 질문 목록(최신순 5개)

    @Getter
    @Builder
    public static class ChatSummary {
        private String question;
        private String answer;
        private LocalDateTime createdAt;
    }

}
