package com.develop.wisebot.domain.guide.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "이용 가이드 응답 DTO")
public class GuideResponse {
    @Schema(description = "이용 가이드 본문", example = "🤖 WiseBot에 오신 것을 환영합니다!\\n\\n- 질문을 입력하면 인공지능이 답변해 드립니다.\\n- 비회원은 하루 3회 질문이 가능합니다.\\n- 회원가입 시 이전 기록 조회, 무제한 질문 등의 기능이 제공됩니다.\\n\\n지금 바로 질문을 시작해보세요!")
    private String guide;

    public GuideResponse(String guide){
        this.guide = guide;
    }
}
