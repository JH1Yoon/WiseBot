package com.develop.wisebot.domain.user.controller;

import com.develop.wisebot.common.message.SuccessMessage;
import com.develop.wisebot.common.message.SuccessResponse;
import com.develop.wisebot.domain.user.dto.request.DeleteRequest;
import com.develop.wisebot.domain.user.dto.request.LoginRequest;
import com.develop.wisebot.domain.user.dto.request.SignupRequest;
import com.develop.wisebot.domain.user.dto.request.UserUpdateRequest;
import com.develop.wisebot.domain.user.dto.response.ChangeResponse;
import com.develop.wisebot.domain.user.dto.response.LoginResponse;
import com.develop.wisebot.domain.user.dto.response.SignupResponse;
import com.develop.wisebot.domain.user.dto.response.searchResponse;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest, @RequestParam(required = false) String adminKey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequest, adminKey));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }

    // 가입한 모든 회원 조회(관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<searchResponse>> search(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.search());
    }

    // 회원 정보 변경
    @PatchMapping("/update")
    public ResponseEntity<ChangeResponse> update(@AuthenticationPrincipal User user, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(user, userUpdateRequest));
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<SuccessResponse> getUser(@AuthenticationPrincipal User user, @RequestBody DeleteRequest deleteRequest) {
        userService.delete(user, deleteRequest);
        return ResponseEntity.status(SuccessMessage.DELETED.getStatus())
                .body(new SuccessResponse(SuccessMessage.DELETED.getStatus().value(), SuccessMessage.DELETED.getMessage(user.getUsername())));
    }
}
