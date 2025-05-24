package com.develop.wisebot.domain.user.repository;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 유저 ID로 조회
    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, "가입된 유저 정보"));
    }

    // 로그인 등 보안 민감한 상황에서 사용
    default User findByEmailOrThrowForLogin(String email) {
        return findByEmail(email).orElseThrow(() ->
                new CustomException(ErrorCode.INVALID_CREDENTIALS)
        );
    }

    // 일반 조회 (예: 관리자 기능, 토큰 필터 등 내부 호출)
    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND, email)
        );
    }

    // 유저이름 존재 여부 확인
    default void throwIfUsernameExists(String username) {
        if (existsByUsername(username)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS, username);
        }
    }
}
