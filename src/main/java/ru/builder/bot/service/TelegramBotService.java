package ru.builder.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private final CheckService checkService;
    private final CallbackHandlerService callbackHandlerService;
    private final TextHandlerService textHandlerService;

    @Autowired
    public TelegramBotService(CheckService checkService, CallbackHandlerService callbackHandlerService, TextHandlerService textHandlerService) {
        this.checkService = checkService;
        this.callbackHandlerService = callbackHandlerService;
        this.textHandlerService = textHandlerService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (checkService.messageIsText(update)) {
            try {
                textHandlerService.handleText(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        if (checkService.massageIsCallbackData(update)) {
            try {
                callbackHandlerService.handleAllCallback(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String getBotUsername() {
        return botUsername; // Возвращаем значение из application.yml
    }

    @Override
    public String getBotToken() {
        return botToken; // Возвращаем значение из application.yml
    }

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;
}
