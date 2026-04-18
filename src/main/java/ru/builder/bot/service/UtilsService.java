package ru.builder.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UtilsService {

    public Long getUniversalChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else {
            return update.getCallbackQuery().getMessage().getChatId();
        }

    }

    public String getUniversalMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        } else {
            return update.getCallbackQuery().getMessage().getText();
        }
    }
}
