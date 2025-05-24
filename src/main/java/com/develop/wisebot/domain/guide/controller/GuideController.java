package com.develop.wisebot.domain.guide.controller;

import com.develop.wisebot.domain.guide.dto.response.GuideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/guides")
public class GuideController {

    @GetMapping()
    public ResponseEntity<GuideResponse> getGuide() {
        String guideText = """
                🤖 WiseBot에 오신 것을 환영합니다!

                - 질문을 입력하면 인공지능이 답변해 드립니다.
                - 비회원은 하루 3회 질문이 가능합니다.
                - 회원가입 시 이전 기록 조회, 무제한 질문 등의 기능이 제공됩니다.

                지금 바로 질문을 시작해보세요!
                """;

        return ResponseEntity.ok(new GuideResponse(guideText));
    }
}