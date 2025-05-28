package com.develop.wisebot.domain.user.service;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.common.jwt.JwtUtil;
import com.develop.wisebot.domain.chat.entity.Chat;
import com.develop.wisebot.domain.chat.repository.ChatRepository;
import com.develop.wisebot.domain.user.dto.request.DeleteRequest;
import com.develop.wisebot.domain.user.dto.request.LoginRequest;
import com.develop.wisebot.domain.user.dto.request.SignupRequest;
import com.develop.wisebot.domain.user.dto.request.UserUpdateRequest;
import com.develop.wisebot.domain.user.dto.response.AdminStatisticsResponse;
import com.develop.wisebot.domain.user.dto.response.LoginResponse;
import com.develop.wisebot.domain.user.dto.response.SearchResponse;
import com.develop.wisebot.domain.user.dto.response.SignupResponse;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import com.develop.wisebot.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "adminKey", "secretAdminKey");
    }

    @Test
    @DisplayName("유저 회원가입 성공")
    void signup_success_userRole() {
        // given
        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "username", "testuser");
        ReflectionTestUtils.setField(request, "password", "1234");
        ReflectionTestUtils.setField(request, "email", "user@test.com");

        when(passwordEncoder.encode("1234")).thenReturn("encodedPwd");

        // when
        SignupResponse response = userService.signup(request, null);

        // then
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRoles().get(0).getRole().name()).isEqualTo(UserRoleEnum.USER.name());
        assertThat(response.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("관리인 회원가입 성공")
    void signup_success_adminRole() {
        // given
        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "username", "admin");
        ReflectionTestUtils.setField(request, "password", "admin123");
        ReflectionTestUtils.setField(request, "email", "admin@test.com");

        when(passwordEncoder.encode("admin123")).thenReturn("encodedPwd");

        // when
        SignupResponse response = userService.signup(request, "secretAdminKey");

        // then
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRoles().get(0).getRole().name()).isEqualTo(UserRoleEnum.ADMIN.name());
        assertThat(response.getEmail()).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("회원가입 실패_잘못된 관리 키")
    void signup_fail_invalidAdminKey() {
        // given
        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "username", "admin");
        ReflectionTestUtils.setField(request, "password", "admin123");
        ReflectionTestUtils.setField(request, "email", "admin@test.com");

        // when & then
        assertThatThrownBy(() -> userService.signup(request, "wrongKey"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_ADMIN_KEY.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "user@test.com");
        ReflectionTestUtils.setField(request, "password", "1234");
        User user = User.builder()
                .username("user")
                .email("user@test.com")
                .password("encodedPwd")
                .role(UserRoleEnum.USER)
                .build();

        when(userRepository.findByEmailOrThrowForLogin("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("1234", "encodedPwd")).thenReturn(true);
        when(jwtUtil.createToken(user.getEmail(), user.getUsername(), user.getRole())).thenReturn("mockToken");

        // when
        LoginResponse response = userService.login(request);

        // then
        assertThat(response.getToken()).isEqualTo("mockToken");
    }

    @Test
    @DisplayName("로그인 실패_잘못된 비밀번호")
    void login_fail_wrongPassword() {
        // given
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "user@test.com");
        ReflectionTestUtils.setField(request, "password", "wrongPwd");

        User user = User.builder().email("user@test.com").password("encodedPwd").build();

        when(userRepository.findByEmailOrThrowForLogin("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongPwd", "encodedPwd")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("가입한 모든 유저 조회 성공")
    void search_success() {
        // given
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .password("pwd1")
                .role(UserRoleEnum.USER)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .password("pwd2")
                .role(UserRoleEnum.ADMIN)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // when
        List<SearchResponse> result = userService.search();

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("회원 정보 변경 성공")
    void update_success_usernameAndPasswordChange() {
        // given
        User user = User.builder().id(1L).username("old").email("user@test.com").build();

        UserUpdateRequest request = new UserUpdateRequest();
        ReflectionTestUtils.setField(request, "username", "newUsername");
        ReflectionTestUtils.setField(request, "password", "newPassword");

        User managedUser = mock(User.class);
        when(userRepository.findByIdOrThrow(1L)).thenReturn(managedUser);

        // when
        userService.update(user, request);

        // then
        verify(managedUser).changeUsername("newUsername");
        verify(managedUser).changePassword("newPassword", passwordEncoder);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void delete_success() {
        // given
        User user = User.builder().id(1L).password("encodedPwd").build();

        DeleteRequest request = new DeleteRequest();
        ReflectionTestUtils.setField(request, "password", "plainPwd");

        User managedUser = mock(User.class);

        when(userRepository.findByIdOrThrow(1L)).thenReturn(managedUser);
        when(passwordEncoder.matches("plainPwd", "encodedPwd")).thenReturn(true);

        // when
        userService.delete(user, request);

        // then
        verify(userRepository).delete(managedUser);
    }

    @Test
    @DisplayName("관리자 통계 항목 조회 성공")
    void getStatistics_success() {
        // given
        LocalDateTime start = LocalDateTime.of(2025, 5, 26, 0, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 5, 27, 0, 0, 0, 0);

        when(chatRepository.countByCreatedAtBetween(start, end)).thenReturn(10L);
        when(userRepository.countByCreatedAtBetween(start, end)).thenReturn(5L);
        when(chatRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of(
                Chat.builder().question("Q1").answer("A1").createdAt(LocalDateTime.now()).build()
        ));

        // when
        AdminStatisticsResponse response = userService.getStatistics();

        // then
        assertThat(response.getTotalChats()).isEqualTo(0L);
        assertThat(response.getTodayUsers()).isEqualTo(5L);
    }
}