package ru.builder.bot.mapper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.builder.bot.data.BotState;
import ru.builder.bot.model.UserEntity;

@Component
public class UserMapper {
    public UserEntity toUserData(Update update, BotState botState) {
        UserEntity userData = new UserEntity();
        userData.setUsername(update.getMessage().getFrom().getUserName());
        userData.setChatId(update.getMessage().getChatId());
        userData.setFirstName(update.getMessage().getFrom().getFirstName());
        userData.setLastName(update.getMessage().getFrom().getLastName());
        userData.setBotState(botState);
        return userData;
    }
}
