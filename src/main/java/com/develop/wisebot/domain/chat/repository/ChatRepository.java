package com.develop.wisebot.domain.chat.repository;

import com.develop.wisebot.common.exception.CustomException;
import com.develop.wisebot.common.exception.ErrorCode;
import com.develop.wisebot.domain.chat.entity.Chat;
import com.develop.wisebot.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByUser(User user, Pageable pageable);

    default Page<Chat> findByUserOrThrow(User user, Pageable pageable) {
        Page<Chat> page = findByUser(user, pageable);
        if (page.isEmpty()) {
            throw new CustomException(ErrorCode.CHAT_IS_EMPTY, user.getEmail());
        }
        return page;
    }
}
