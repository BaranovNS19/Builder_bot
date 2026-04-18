package ru.builder.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.builder.bot.data.BotState;
import ru.builder.bot.mapper.UserMapper;
import ru.builder.bot.model.UserEntity;
import ru.builder.bot.repository.UserRepository;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void saveUser(Update update, BotState botState) {
        log.info("сохранение пользователя");
        userRepository.save(userMapper.toUserData(update, botState));
    }

    public BotState getBotStateByChatId(Update update) {
        Long chatId = null;
        if (update.hasMessage() && update.getMessage() != null) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        UserEntity userData = userRepository.findByChatId(chatId);
        log.info("получен пользователь {}", userData);
        return userData.getBotState();
    }

    public UserEntity getUserByChatId(Update update) {
        Long chatId = null;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        return userRepository.findByChatId(chatId);
    }

    public String getFromMessage(Update update) {
        String userName = null;
        if (update.hasMessage()) {
            userName = update.getMessage().getFrom().getUserName();
        } else if (update.hasCallbackQuery()) {
            userName = update.getCallbackQuery().getMessage().getFrom().getUserName();
        }
        return userName;
    }

    public void updateUserState(Update update, BotState botState) {
        UserEntity userData = getUserByChatId(update);
        userData.setBotState(botState);
        userRepository.save(userData);
    }

    public String getUserName(Update update) {
        return getFromMessage(update);
    }
}
