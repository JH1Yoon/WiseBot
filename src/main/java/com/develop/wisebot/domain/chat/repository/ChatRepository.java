package com.develop.wisebot.domain.chat.repository;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.domain.chat.entity.Chat;
import com.develop.wisebot.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findById(Long chatId);

    default Chat findByIdOrThrow(Long chatId) {
        return findById(chatId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, Long.toString(chatId)));
    }

    Page<Chat> findByUser(User user, Pageable pageable);

    default Page<Chat> findByUserOrThrow(User user, Pageable pageable) {
        Page<Chat> page = findByUser(user, pageable);
        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.CHAT_IS_EMPTY, user.getEmail());
        }
        return page;
    }

    Page<Chat> findByUserAndQuestionContainingIgnoreCaseOrUserAndAnswerContainingIgnoreCase(
            User user1, String keyword1, User user2, String keyword2, Pageable pageable);

    default Page<Chat> findByUserAndKeyword(User user, String keyword, Pageable pageable) {
        return findByUserAndQuestionContainingIgnoreCaseOrUserAndAnswerContainingIgnoreCase(
                user, keyword, user, keyword, pageable
        );
    }
}