package com.develop.wisebot.domain.controller;

import com.develop.wisebot.common.message.SuccessMessage;
import com.develop.wisebot.common.message.SuccessResponse;
import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.service.ChatService;
import com.develop.wisebot.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    // 채팅 저장
    @PostMapping
    public ResponseEntity<ChatResponse> saveChat(@AuthenticationPrincipal User user, @RequestBody ChatRequest chatRequest) {
        return ResponseEntity.ok(chatService.save(user, chatRequest));
    }

    // 채팅 이력 조회
    @GetMapping
    public ResponseEntity<Page<ChatResponse>> getChats(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(chatService.getAll(user, pageable));
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
