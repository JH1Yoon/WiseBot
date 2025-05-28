package com.develop.wisebot.domain.chat.controller;

import com.develop.wisebot.common.exception.ErrorResponse;
import com.develop.wisebot.common.message.SuccessMessage;
import com.develop.wisebot.common.message.SuccessResponse;
import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.dto.response.PageResponse;
import com.develop.wisebot.domain.chat.service.ChatService;
import com.develop.wisebot.domain.user.dto.response.SignupResponse;
import com.develop.wisebot.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;


@RestController
@RequiredArgsConstructor
@RequestMapping("v1/chats")
public class ChatController {
    private final ChatService chatService;

    // 채팅
    @PostMapping
    @Operation(summary = "채팅", description = "회원/비회원 채팅합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatResponse.class)))
            })
    @ApiResponse(responseCode = "429", description = "",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":429,\"message\":\"게스트는 하루에 최대 3번 질문할 수 있습니다.\",\"status\":\"TOO_MANY_REQUESTS\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ChatResponse> saveChat(
            @AuthenticationPrincipal User user,
            @RequestBody ChatRequest chatRequest,
            HttpServletRequest request) {
        return ResponseEntity.ok(chatService.save(user, chatRequest, request));
    }

    // 회원의 채팅 단일 조회
    @GetMapping("/{chatId}")
    @Operation(summary = "채팅 단건 조회", description = "사용자의 단일 채팅 내역을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅 단건 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatResponse.class)))
            })
    @ApiResponse(responseCode = "404", description = "채팅 조회 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":404,\"message\":\"1을(를) 찾지못했습니다.\",\"status\":\"NOT_FOUND\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ChatResponse> getChatById(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Chat ID", example = "1") @PathVariable Long chatId
    ) {
        return ResponseEntity.ok(chatService.getById(user, chatId));
    }

    // 회원의 채팅 이력 모두 조회
    @GetMapping
    @Operation(
            summary = "채팅 목록 조회",
            description = "사용자의 채팅 목록을 페이지네이션 및 키워드로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
            }
    )
    @ApiResponse(responseCode = "401", description = "로그인 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":401,\"message\":\"로그인이 필요합니다.\",\"status\":\"UNAUTHORIZED\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<PageResponse<ChatResponse>> getChats(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Keyword", name = "키워드", example = "부트") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(hidden = true) @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.from(chatService.getAll(user, keyword, pageable)));
    }

    // 채팅 삭제
    @DeleteMapping("/{chatId}")
    @Operation(summary = "채팅 삭제", description = "사용자의 단일 채팅을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"code\":200,\"message\":\"wiseadmin@example.com의 1 채팅을 삭제했습니다.\"}"),
                                    schema = @Schema(implementation = SuccessResponse.class)))
            })
    @ApiResponse(responseCode = "401", description = "채팅 접근 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":401,\"message\":\"본인의 채팅만 접근할 수 있습니다.\",\"status\":\"UNAUTHORIZED\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<SuccessResponse> deleteChat(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Chat ID", example = "1") @PathVariable Long chatId) {
        chatService.deleteChat(user, chatId);

        String msg = SuccessMessage.CHAT_DELETED.getMessage(user.getEmail(), chatId.toString());

        return ResponseEntity.status(SuccessMessage.CHAT_DELETED.getStatus())
                .body(new SuccessResponse(SuccessMessage.CHAT_DELETED.getStatus().value(), msg));
    }
}
