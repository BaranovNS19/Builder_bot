package ru.builder.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.builder.bot.data.BotState;

import java.time.LocalDate;

@Service
public class CheckService {

    private final UserService userService;

    @Autowired
    public CheckService(UserService userService) {
        this.userService = userService;
    }

    public boolean messageIsText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public boolean massageIsCallbackData(Update update) {
        return update.hasCallbackQuery();
    }

    public boolean dateInThePast(LocalDate localDate) {
        return localDate.isBefore(LocalDate.now());
    }

    public boolean messageIsCommandStart(Update update) {
        return update.getMessage().getText().equals("/start");
    }

    public boolean botStateIs(Update update, BotState botState) {
        return userService.getBotStateByChatId(update).equals(botState);
    }
}
