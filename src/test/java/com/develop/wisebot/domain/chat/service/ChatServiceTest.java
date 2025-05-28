package com.develop.wisebot.domain.chat.service;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.entity.Chat;
import com.develop.wisebot.domain.chat.repository.ChatRepository;
import com.develop.wisebot.domain.openai.service.OpenAiService;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private OpenAiService openAiService;

    @Mock
    private HttpServletRequest request;

    private final User testUser = User.builder()
            .id(1L)
            .username("tester")
            .email("test@example.com")
            .password("encoded")
            .role(UserRoleEnum.USER)
            .build();


    @Test
    @DisplayName("채팅(회원) 성공")
    void saveChatUser_success() {
        // given
        ChatRequest chatRequest = new ChatRequest();
        ReflectionTestUtils.setField(chatRequest, "question", "What is AI?");

        Chat chat = Chat.builder()
                .user(testUser)
                .question("What is AI?")
                .answer("AI is artificial intelligence.")
                .build();

        when(chatRepository.save(any(Chat.class))).thenReturn(chat);
        when(openAiService.askToGpt("What is AI?")).thenReturn("AI is artificial intelligence.");

        // when
        ChatResponse response = chatService.save(testUser, chatRequest, request);

        // then
        assertThat(response.getQuestion()).isEqualTo("What is AI?");
        assertThat(response.getAnswer()).isEqualTo("AI is artificial intelligence.");
    }

    @Test
    @DisplayName("채팅(비회원) 성공")
    void saveChatGuest_success() {
        // given
        ChatRequest chatRequest = new ChatRequest();
        ReflectionTestUtils.setField(chatRequest, "question", "Hello?");

        when(openAiService.askToGpt("Hello?")).thenReturn("Hi there!");
        when(request.getSession()).thenReturn(mock(HttpSession.class));
        when(request.getSession().getId()).thenReturn("guest-session");

        // when
        ChatResponse response = chatService.save(null, chatRequest, request);

        // then
        assertThat(response.getQuestion()).isEqualTo("Hello?");
        assertThat(response.getAnswer()).isEqualTo("Hi there!");
    }

    @Test
    @DisplayName("채팅 실패_비회원 사용량 초과")
    void saveChat_fail_givenGuestExceedsLimit() {
        // given
        ChatRequest chatRequest = new ChatRequest();
        ReflectionTestUtils.setField(chatRequest, "question", "Hello?");

        when(request.getSession()).thenReturn(mock(HttpSession.class));
        when(request.getSession().getId()).thenReturn("guest-session");

        for (int i = 0; i < 3; i++) {
            chatService.save(null, chatRequest, request);
        }

        // when & then
        CustomException ex = assertThrows(CustomException.class,
                () -> chatService.save(null, chatRequest, request));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.GUEST_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("회원 채팅 단일 조회 성공")
    void getById_success() {
        // given
        Chat chat = Chat.builder()
                .id(1L)
                .user(testUser)
                .question("Q?")
                .answer("A.")
                .build();

        when(chatRepository.findByIdOrThrow(1L)).thenReturn(chat);

        // when
        ChatResponse response = chatService.getById(testUser, 1L);

        // then
        assertThat(response.getQuestion()).isEqualTo("Q?");
        assertThat(response.getAnswer()).isEqualTo("A.");
    }

    @Test
    @DisplayName("회원 채팅 단일 조회 실패_소유하지 않은 채팅")
    void givenChatNotOwnedByUser_whenGetById_thenThrowsCustomException() {
        // given
        User otherUser = User.builder().id(2L).build();
        Chat chat = Chat.builder()
                .id(1L)
                .user(otherUser)
                .question("Q")
                .answer("A")
                .build();

        when(chatRepository.findByIdOrThrow(1L)).thenReturn(chat);

        // when & then
        assertThrows(CustomException.class, () -> chatService.getById(testUser, 1L));
    }

    @Test
    @DisplayName("채팅 이력 모두 조회 성공")
    void givenUserAndKeyword_whenGetAll_thenReturnsFilteredPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Chat chat = Chat.builder().question("Q").answer("A").user(testUser).build();
        Page<Chat> chatPage = new PageImpl<>(List.of(chat));

        when(chatRepository.findByUserAndKeyword(testUser, "Q", pageable)).thenReturn(chatPage);

        // when
        Page<ChatResponse> result = chatService.getAll(testUser, "Q", pageable);

        // then
        assertThat(result.getContent().get(0).getQuestion()).isEqualTo("Q");
    }

    @Test
    @DisplayName("채팅 이력 모두 조회 실패_사용자 null")
    void givenNullUser_whenGetAll_thenThrowsUnauthorizedException() {
        // when & Then
        assertThrows(CustomException.class,
                () -> chatService.getAll(null, null, PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("채팅 삭제_성공")
    void givenOwnerUser_whenDeleteChat_thenChatDeleted() {
        // given
        Chat chat = Chat.builder().id(1L).user(testUser).build();
        when(chatRepository.findByIdOrThrow(1L)).thenReturn(chat);

        // when
        chatService.deleteChat(testUser, 1L);

        // then
        verify(chatRepository).delete(chat);
    }

    @Test
    @DisplayName("채팅 삭제 실패_소유하지 않은 채팅")
    void givenNonOwnerUser_whenDeleteChat_thenThrowsCustomException() {
        // given
        Chat chat = Chat.builder().id(1L).user(User.builder().id(99L).build()).build();
        when(chatRepository.findByIdOrThrow(1L)).thenReturn(chat);

        // when & then
        assertThrows(CustomException.class, () -> chatService.deleteChat(testUser, 1L));
    }
}