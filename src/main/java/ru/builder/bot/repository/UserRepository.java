package ru.builder.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.builder.bot.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByChatId(Long chatId);
}
