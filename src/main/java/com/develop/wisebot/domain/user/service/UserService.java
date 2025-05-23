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
import com.develop.wisebot.domain.user.dto.response.*;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.entity.UserRoleEnum;
import com.develop.wisebot.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.key}")
    private String adminKey;

    /**
     * 회원가입
     *
     * @param signupRequest
     * @param inputAdminKey
     * @return SignupResponse
     */
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest, String inputAdminKey) {
        // 중복 사용자 검증
        userRepository.throwIfUsernameExists(signupRequest.getUsername());

        // 역할 결정
        UserRoleEnum role = UserRoleEnum.USER;
        if (inputAdminKey != null) {
            if (inputAdminKey.equals(adminKey)) {
                role = UserRoleEnum.ADMIN;
            } else {
                throw new CustomException(ErrorCode.INVALID_ADMIN_KEY);
            }
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 사용자 생성 및 저장
        User user = User.builder().
                username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(encodedPassword)
                .role(role)
                .build();

        userRepository.save(user);

        // 응답 생성
        SignupResponse.RoleDto roleDto = new SignupResponse.RoleDto(role);
        return new SignupResponse(user.getUsername(), user.getEmail(), List.of(roleDto));
    }

    /**
     * 로그인
     *
     * @param loginRequest
     * @return LoginResponse
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailOrThrowForLogin(loginRequest.getEmail());

        checkPassword(loginRequest.getPassword(), user.getPassword());

        UserRoleEnum role = user.getRole();

        return new LoginResponse(jwtUtil.createToken(user.getEmail(), user.getUsername(), role));
    }

    /**
     * 가입한 모든 유저 조회(관리자만)
     *
     * @return List<searchResponse>
     */
    public List<searchResponse> search() {
        return userRepository.findAll().stream()
                .map(searchResponse::from)
                .toList();
    }

    /**
     * 회원 정보 변경
     *
     * @param user
     * @param userUpdateRequest
     * @return ChangeResponse
     */
    @Transactional
    public ChangeResponse update(User user, UserUpdateRequest userUpdateRequest) {

        User managedUser = userRepository.findByIdOrThrow(user.getId());

        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().isBlank()) {
            userRepository.throwIfUsernameExists(userUpdateRequest.getUsername());
            managedUser.changeUsername(userUpdateRequest.getUsername());
        }

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isBlank()) {
            managedUser.changePassword(userUpdateRequest.getPassword(), passwordEncoder);
        }

        ChangeResponse.RoleDto roleDto = new ChangeResponse.RoleDto(managedUser.getRole());
        return new ChangeResponse(managedUser.getUsername(), managedUser.getEmail(), List.of(roleDto));
    }

    /**
     * 회원 탈퇴
     *
     * @param user
     * @param deleteRequest
     * @return
     */
    @Transactional
    public void delete(User user, DeleteRequest deleteRequest) {
        User managedUser = userRepository.findByIdOrThrow(user.getId());

        checkPassword(deleteRequest.getPassword(), user.getPassword());

        userRepository.delete(managedUser);
    }


    /** 관리자 통계 항목 조회
     *
     * @return AdminStatisticsResponse
     */
    public AdminStatisticsResponse getStatistics() {
        long totalChats = chatRepository.count();
        long totalUsers = userRepository.count();

        long todayChats = chatRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1)
        );

        long todayUsers = userRepository.countByCreatedAtBetween(
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atStartOfDay().plusDays(1)
        );

        List<Chat> recentChats = chatRepository.findTop5ByOrderByCreatedAtDesc();

        List<AdminStatisticsResponse.ChatSummary> recentChatSummaries = recentChats.stream()
                .map(chat -> AdminStatisticsResponse.ChatSummary.builder()
                        .question(chat.getQuestion())
                        .answer(chat.getAnswer())
                        .createdAt(chat.getCreatedAt())
                        .build()).toList();

        return AdminStatisticsResponse.builder()
                .totalChats(totalChats)
                .totalUsers(totalUsers)
                .todayChats(todayChats)
                .todayUsers(todayUsers)
                .recentChats(recentChatSummaries)
                .build();

    }

    // 비밀번호 확인
    private void checkPassword(String requestPassword, String userPassword) {
        if (!passwordEncoder.matches(requestPassword, userPassword)) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
