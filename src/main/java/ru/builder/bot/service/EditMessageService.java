package ru.builder.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class EditMessageService {

    private final SendMessageService sendMessageService;

    @Autowired
    public EditMessageService(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    /**
     * Редактируем текст и меняем клавиатуру
     */
    public void editAndChangeKeyboard(Update update, String newText, InlineKeyboardMarkup newKeyboard) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(newKeyboard);

        try {
            sendMessageService.sendMessage(editMessage);

        } catch (TelegramApiException e) {
            log.info(String.valueOf(e));
        }
    }

    /**
     * Редактируем текст и убираем клавиатуру
     */
    public Integer editAndRemoveKeyboard(Update update, String newText) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);

        try {
            sendMessageService.sendMessage(editMessage);
        } catch (TelegramApiException e) {
            log.info(String.valueOf(e));
        }
        return messageId;
    }
}
