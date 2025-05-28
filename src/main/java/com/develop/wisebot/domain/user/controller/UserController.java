package com.develop.wisebot.domain.user.controller;

import com.develop.wisebot.common.exception.ErrorResponse;
import com.develop.wisebot.common.message.SuccessMessage;
import com.develop.wisebot.common.message.SuccessResponse;
import com.develop.wisebot.domain.user.dto.request.DeleteRequest;
import com.develop.wisebot.domain.user.dto.request.LoginRequest;
import com.develop.wisebot.domain.user.dto.request.SignupRequest;
import com.develop.wisebot.domain.user.dto.request.UserUpdateRequest;
import com.develop.wisebot.domain.user.dto.response.*;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "회원 관련 API")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "일반 사용자 또는 관리자 회원가입을 수행합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignupResponse.class)))
            })
    @ApiResponse(responseCode = "409", description = "이미 존재하는 회원",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":409,\"message\":\"wiseadmin는 이미 존재합니다.\",\"status\":\"CONFLICT\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "관리자 키 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":401,\"message\":\"입력한 키가 관리자 키와 맞지않습니다.\",\"status\":\"UNAUTHORIZED\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest,
                                                 @Parameter(description = "관리자 키 (관리자 회원가입 시에만 필요)", example = "3jF#dK91!ZmPq4tX", required = false)
                                                 @RequestParam(required = false) String adminKey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequest, adminKey));
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호로 로그인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)))
            })
    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":401,\"message\":\"아이디 또는 비밀번호가 올바르지 않습니다.\",\"status\":\"UNAUTHORIZED\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }

    // 가입한 모든 회원 조회(관리자만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    @Operation(summary = "회원 전체 조회 (관리자 전용)", description = "관리자 권한으로 모든 가입된 회원 정보를 조회합니다.",
    responses = {
            @ApiResponse(responseCode = "200", description = "회원 전체 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SearchResponse.class)))
    })
    @ApiResponse(responseCode = "403", description = "접근 거부",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":403,\"message\":\"접근이 거부되었습니다.\",\"status\":\"FORBIDDEN\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<SearchResponse>> search(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.search());
    }

    // 회원 정보 변경
    @PatchMapping("/update")
    @Operation(summary = "회원 정보 변경", description = "회원의 닉네임 또는 비밀번호를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 변경 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChangeResponse.class)))
            })
    @ApiResponse(responseCode = "404", description = "회원의 정보가 발견되지 않음",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":404,\"message\":\"가입된 유저 정보을(를) 찾지못했습니다.\",\"status\":\"NOT_FOUND\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ChangeResponse> update(@AuthenticationPrincipal User user, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(user, userUpdateRequest));
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "회원이 탈퇴를 요청합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"code\":200,\"message\":\"newWiseAdmin을(를) 삭제했습니다.\"}"),
                                    schema = @Schema(implementation = SuccessResponse.class)))
            })
    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":401,\"message\":\"아이디 또는 비밀번호가 올바르지 않습니다.\",\"status\":\"UNAUTHORIZED\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<SuccessResponse> getUser(@AuthenticationPrincipal User user, @RequestBody DeleteRequest deleteRequest) {
        userService.delete(user, deleteRequest);
        return ResponseEntity.status(SuccessMessage.DELETED.getStatus())
                .body(new SuccessResponse(SuccessMessage.DELETED.getStatus().value(), SuccessMessage.DELETED.getMessage(user.getUsername())));
    }

    // 관리자 통계 항목 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/statistics")
    @Operation(summary = "관리자 통계 조회", description = "가입자 수, 질문 수 등의 관리자 통계 항목을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "관리자 통계 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AdminStatisticsResponse.class)))
            })
    @ApiResponse(responseCode = "403", description = "접근 거부",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"code\":403,\"message\":\"접근이 거부되었습니다.\",\"status\":\"FORBIDDEN\"}"),
                    schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AdminStatisticsResponse> getStatistics(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getStatistics());
    }
}
