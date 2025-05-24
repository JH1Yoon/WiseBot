package com.develop.wisebot.domain.chat.controller;

import com.develop.wisebot.common.message.SuccessMessage;
import com.develop.wisebot.common.message.SuccessResponse;
import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.dto.response.PageResponse;
import com.develop.wisebot.domain.chat.service.ChatService;
import com.develop.wisebot.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<ChatResponse> saveChat(
            @AuthenticationPrincipal User user,
            @RequestBody ChatRequest chatRequest,
            HttpServletRequest request) {
        return ResponseEntity.ok(chatService.save(user, chatRequest, request));
    }

    // 회원의 채팅 단일 조회
    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatById(
            @AuthenticationPrincipal User user,
            @PathVariable Long chatId
    ) {
        return ResponseEntity.ok(chatService.getById(user, chatId));
    }

    // 회원의 채팅 이력 모두 조회
    @GetMapping
    public ResponseEntity<PageResponse<ChatResponse>> getChats(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.from(chatService.getAll(user, keyword, pageable)));
    }

    // 채팅 삭제
    @DeleteMapping("/{chatId}")
    public ResponseEntity<SuccessResponse> deleteChat(
            @AuthenticationPrincipal User user, @PathVariable Long chatId) {
        chatService.deleteChat(user, chatId);

        String msg = SuccessMessage.CHAT_DELETED.getMessage(user.getEmail(), chatId.toString());

        return ResponseEntity.status(SuccessMessage.CHAT_DELETED.getStatus())
                .body(new SuccessResponse(SuccessMessage.CHAT_DELETED.getStatus().value(), msg));
    }
}
