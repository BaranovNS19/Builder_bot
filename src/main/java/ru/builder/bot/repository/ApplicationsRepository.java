package ru.builder.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.builder.bot.model.ApplicationEntity;

import java.util.List;

public interface ApplicationsRepository extends JpaRepository<ApplicationEntity, Long> {

    List<ApplicationEntity> findByChatId(Long chatId);
}
