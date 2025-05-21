package com.develop.wisebot.domain.chat.service;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.entity.Chat;
import com.develop.wisebot.domain.chat.repository.ChatRepository;
import com.develop.wisebot.domain.openai.service.OpenAiService;
import com.develop.wisebot.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRepository chatRepository;
    private final OpenAiService openAiService;

    /** 채팅 저장
     *
     * @param user
     * @param chatRequest
     * @return ChatResponse
     */
    @Transactional
    public ChatResponse save(User user, ChatRequest chatRequest) {
        String mockAnswer = openAiService.askToGpt(chatRequest.getQuestion());

        // 로그인한 사용자일 때만 저장
        if (user != null) {
            Chat chat = Chat.builder()
                    .user(user)
                    .question(chatRequest.getQuestion())
                    .answer(mockAnswer)
                    .build();

            Chat savedChat = chatRepository.save(chat);

            return ChatResponse.builder()
                    .question(savedChat.getQuestion())
                    .answer(savedChat.getAnswer())
                    .createdAt(savedChat.getCreatedAt())
                    .build();
        }

        // 비회원이면 저장 없이 응답만 반환
        return ChatResponse.builder()
                .question(chatRequest.getQuestion())
                .answer(mockAnswer)
                .createdAt(null)
                .build();
    }

    /** 채팅 이력 조회
     *
     * @param user
     * @param pageable
     * @return Page<ChatResponse>
     */
    public Page<ChatResponse> getAll(User user, Pageable pageable) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return chatRepository.findByUserOrThrow(user, pageable)
                .map(chat -> ChatResponse.builder()
                        .question(chat.getQuestion())
                        .answer(chat.getAnswer())
                        .createdAt(chat.getCreatedAt())
                        .build());
    }

    /** 채팅 삭제
     * 
     * @param user
     * @param chatId
     */
    @Transactional
    public void deleteChat(User user, Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "Chat id: " + chatId));

        if (!chat.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CHAT_INVALID_ACCESS);
        }

        chatRepository.delete(chat);
    }
}
