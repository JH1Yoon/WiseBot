package com.develop.wisebot.domain.chat.controller;

import com.develop.wisebot.domain.chat.dto.request.ChatRequest;
import com.develop.wisebot.domain.chat.dto.response.ChatResponse;
import com.develop.wisebot.domain.chat.service.ChatService;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatControllerTest {

    @InjectMocks
    private ChatController chatController;

    @Mock
    private ChatService chatService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User adminUser;
    private User user;

    private ChatRequest chatRequest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).setCustomArgumentResolvers(pageableResolver).build();
        adminUser = User.builder().id(1L).username("admin")
                .email("admin@email.com").password("123456")
                .role(UserRoleEnum.ADMIN).build();

        user = User.builder().id(1L).username("user")
                .email("user@email.com").password("123456")
                .role(UserRoleEnum.USER).build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        adminUser,
                        null,
                        List.of(() -> adminUser.getRole().getAuthority())
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    @DisplayName("채팅 저장 성공")
    void saveChat_success() throws Exception {
        // given
        chatRequest = new ChatRequest();
        ReflectionTestUtils.setField(chatRequest, "question", "AI란?");
        ChatResponse chatResponse = ChatResponse.builder()
                .question("AI란?")
                .answer("AI는 인공지능입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        given(chatService.save(
                eq(adminUser),
                any(ChatRequest.class),
                any(HttpServletRequest.class)
        )).willReturn(chatResponse);

        // when & then
        mockMvc.perform(post("/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(chatRequest)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("채팅 단건 조회 성공")
    void getChatById_success() throws Exception {
        // given
        Long chatId = 1L;
        ChatResponse response = ChatResponse.builder()
                .question("AI란?")
                .answer("AI는 인공지능입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        given(chatService.getById(adminUser, chatId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/v1/chats/{chatId}", chatId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("채팅 전체 조회 성공")
    void getChats_success() throws Exception {
        // given
        List<ChatResponse> responseList = List.of(
                ChatResponse.builder()
                        .question("AI란?")
                        .answer("AI는 인공지능을 의미합니다.")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatResponse> page = new PageImpl<>(responseList, pageable, responseList.size());

        given(chatService.getAll(any(User.class), any(), any(Pageable.class))).willReturn(page);

        // when & then
        mockMvc.perform(get("/v1/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].question").value("AI란?"))
                .andExpect(jsonPath("$.content[0].answer").value("AI는 인공지능을 의미합니다."));
    }


    @Test
    @DisplayName("채팅 삭제 성공")
    void deleteChat_success() throws Exception {
        // given
        Long chatId = 1L;
        doNothing().when(chatService).deleteChat(adminUser, chatId);

        // when & then
        mockMvc.perform(delete("/v1/chats/{chatId}", chatId))
                .andExpect(status().isOk());
    }
}