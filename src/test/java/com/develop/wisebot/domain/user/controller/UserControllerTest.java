package com.develop.wisebot.domain.user.controller;

import com.develop.wisebot.domain.user.dto.request.DeleteRequest;
import com.develop.wisebot.domain.user.dto.request.LoginRequest;
import com.develop.wisebot.domain.user.dto.request.SignupRequest;
import com.develop.wisebot.domain.user.dto.response.*;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import com.develop.wisebot.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private HandlerMethodArgumentResolver handlerMethodArgumentResolver;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User mockUser;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(handlerMethodArgumentResolver)
                .build();

        objectMapper = new ObjectMapper();

        signupRequest = new SignupRequest();
        ReflectionTestUtils.setField(signupRequest, "email", "test@example.com");
        ReflectionTestUtils.setField(signupRequest, "password", "password123");
        ReflectionTestUtils.setField(signupRequest, "username", "testuser");

        loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", "test@example.com");
        ReflectionTestUtils.setField(loginRequest, "password", "password123");

        mockUser = User.builder()
                .username("testuser")
                .password("password")
                .email("testuser@eamil.com")
                .role(UserRoleEnum.USER)
                .build();

        when(handlerMethodArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(handlerMethodArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(mockUser);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        // given
        SignupResponse mockResponse = new SignupResponse("testUser", "nickname", List.of(new SignupResponse.RoleDto(UserRoleEnum.USER)));

        when(userService.signup(any(SignupRequest.class), anyString())).thenReturn(mockResponse);

        // when
        mockMvc.perform(post("/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser\", \"password\": \"password123\", \"nickname\": \"nickname\"}"))
                // then
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // given
        String expectedToken = "token123";
        when(userService.login(any(LoginRequest.class))).thenReturn(new LoginResponse(expectedToken));

        // when
        ResponseEntity<LoginResponse> responseEntity = userController.login(loginRequest);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedToken, responseEntity.getBody().getToken());
    }

    @Test
    @DisplayName("회원 조회 (관리자만)")
    void search_success() throws Exception {
        // given
        User mockUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(UserRoleEnum.USER)
                .build();

        SearchResponse mockResponse = SearchResponse.from(mockUser);

        when(userService.search()).thenReturn(List.of(mockResponse));

        // when
        mockMvc.perform(get("/v1/users/search")
                        .header("Authorization", "Bearer some_valid_token"))  // Mocked admin token
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }


    @Test
    @DisplayName("회원 정보 업데이트")
    void update_success() throws Exception {
        // given
        String updatedUsername = "updateduser";
        String updatedEmail = "updated@example.com";
        ChangeResponse mockResponse = new ChangeResponse(updatedUsername, updatedEmail,
                Arrays.asList(new ChangeResponse.RoleDto(UserRoleEnum.USER)));

        when(userService.update(any(), any())).thenReturn(mockResponse);

        // when
        mockMvc.perform(patch("/v1/users/update")
                        .contentType("application/json")
                        .content("{\"username\":\"updateduser\", \"password\":\"newpassword\"}"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedUsername))
                .andExpect(jsonPath("$.email").value(updatedEmail));
    }


    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_success() throws Exception {
        // given
        DeleteRequest request = new DeleteRequest();
        ReflectionTestUtils.setField(request, "password", "rawPassword");
        doNothing().when(userService).delete(any(User.class), any(DeleteRequest.class));

        // when & then
        mockMvc.perform(delete("/v1/users/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("testuser을(를) 삭제했습니다."));
    }


    @Test
    @DisplayName("관리자 통계 조회 성공")
    @WithMockUser(roles = "ADMIN") // ADMIN 권한 부여
    void get_statistics_success() throws Exception {
        // given
        AdminStatisticsResponse.ChatSummary chat1 = AdminStatisticsResponse.ChatSummary.builder()
                .question("질문 1")
                .answer("답변 1")
                .createdAt(LocalDateTime.now())
                .build();

        AdminStatisticsResponse.ChatSummary chat2 = AdminStatisticsResponse.ChatSummary.builder()
                .question("질문 2")
                .answer("답변 2")
                .createdAt(LocalDateTime.now())
                .build();

        AdminStatisticsResponse response = AdminStatisticsResponse.builder()
                .totalChats(100)
                .totalUsers(50)
                .todayChats(5)
                .todayUsers(2)
                .recentChats(List.of(chat1, chat2))
                .build();

        when(userService.getStatistics()).thenReturn(response);
        when(handlerMethodArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(handlerMethodArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(mockUser);

        // when & then
        mockMvc.perform(get("/v1/users/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalChats").value(100))
                .andExpect(jsonPath("$.totalUsers").value(50))
                .andExpect(jsonPath("$.todayChats").value(5))
                .andExpect(jsonPath("$.todayUsers").value(2))
                .andExpect(jsonPath("$.recentChats[0].question").value("질문 1"))
                .andExpect(jsonPath("$.recentChats[0].answer").value("답변 1"))
                .andExpect(jsonPath("$.recentChats[1].question").value("질문 2"))
                .andExpect(jsonPath("$.recentChats[1].answer").value("답변 2"));
    }
}