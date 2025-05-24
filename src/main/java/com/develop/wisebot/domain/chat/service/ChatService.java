package com.develop.wisebot.domain.chat.service;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.entity.Chat;
import com.develop.wisebot.domain.chat.entity.GuestUsage;
import com.develop.wisebot.domain.chat.repository.ChatRepository;
import com.develop.wisebot.domain.openai.service.OpenAiService;
import com.develop.wisebot.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private static final int GUEST_DAILY_LIMIT = 3;

    private final ChatRepository chatRepository;
    private final OpenAiService openAiService;

    private final Map<String, GuestUsage> guestUsageMap = new ConcurrentHashMap<>();


    /**
     * 채팅
     *
     * @param user
     * @param chatRequest
     * @return ChatResponse
     */
    @Transactional
    public ChatResponse save(User user, ChatRequest chatRequest, HttpServletRequest request) {
        String answer = openAiService.askToGpt(chatRequest.getQuestion());

        // 회원
        if (user != null) {
            Chat chat = Chat.builder()
                    .user(user)
                    .question(chatRequest.getQuestion())
                    .answer(answer)
                    .build();
            Chat savedChat = chatRepository.save(chat);

            return ChatResponse.builder()
                    .question(savedChat.getQuestion())
                    .answer(savedChat.getAnswer())
                    .createdAt(savedChat.getCreatedAt())
                    .build();
        }

        // 비회원
        String guestKey = request.getSession().getId(); // 또는 request.getRemoteAddr();
        GuestUsage usage = guestUsageMap.getOrDefault(guestKey, new GuestUsage());

        if (!LocalDate.now().equals(usage.getLastUsedDate())) {
            usage.reset();
        }

        if (usage.getCount() >= GUEST_DAILY_LIMIT) {
            throw new CustomException(ErrorCode.GUEST_LIMIT_EXCEEDED);
        }

        usage.increment();
        guestUsageMap.put(guestKey, usage);

        return ChatResponse.builder()
                .question(chatRequest.getQuestion())
                .answer(answer)
                .createdAt(null)
                .build();
    }

    // 회원의 채팅 단일 조회
    public ChatResponse getById(User user, Long chatId) {
        Chat chat = chatRepository.findByIdOrThrow(chatId);

        if (!chat.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CHAT_INVALID_ACCESS);
        }

        return ChatResponse.builder()
                .question(chat.getQuestion())
                .answer(chat.getAnswer())
                .createdAt(chat.getCreatedAt())
                .build();
    }

    /**
     * 회원의 채팅 이력 모두 조회
     *
     * @param user
     * @param keyword
     * @param pageable
     * @return Page<ChatResponse>
     */
    public Page<ChatResponse> getAll(User user, String keyword, Pageable pageable) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Page<Chat> chats;

        if (keyword == null || keyword.isBlank()) {
            chats = chatRepository.findByUserOrThrow(user, pageable);
        } else {
            chats = chatRepository.findByUserAndKeyword(user, keyword, pageable);
        }

        return chats.map(chat -> ChatResponse.builder()
                .question(chat.getQuestion())
                .answer(chat.getAnswer())
                .createdAt(chat.getCreatedAt())
                .build());
    }

    /**
     * 채팅 삭제
     *
     * @param user
     * @param chatId
     */
    @Transactional
    public void deleteChat(User user, Long chatId) {
        Chat chat = chatRepository.findByIdOrThrow(chatId);

        if (!chat.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CHAT_INVALID_ACCESS);
        }

        chatRepository.delete(chat);
    }
}
