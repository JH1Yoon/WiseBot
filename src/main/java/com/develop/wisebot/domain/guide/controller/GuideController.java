package com.develop.wisebot.domain.guide.controller;

import com.develop.wisebot.domain.guide.dto.response.GuideResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "이용 가이드 조회",
            description = "WiseBot 이용 가이드를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "가이드 반환 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GuideResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "guide": "🤖 WiseBot에 오신 것을 환영합니다!\\n\\n- 질문을 입력하면 인공지능이 답변해 드립니다.\\n- 비회원은 하루 3회 질문이 가능합니다.\\n- 회원가입 시 이전 기록 조회, 무제한 질문 등의 기능이 제공됩니다.\\n\\n지금 바로 질문을 시작해보세요!"
                                            }
                                            """)
                            )
                    )
            }
    )
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