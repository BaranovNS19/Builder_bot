package ru.builder.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class SendMessageService {

    private final TelegramBotService bot;
    private final UtilsService utilsService;

    @Autowired
    public SendMessageService(@Lazy TelegramBotService bot, UtilsService utilsService) {
        this.bot = bot;
        this.utilsService = utilsService;
    }

    public void sendMessage(EditMessageReplyMarkup edit) throws TelegramApiException {
        bot.execute(edit);
    }

    public void sendMessage(AnswerCallbackQuery answer) throws TelegramApiException {
        bot.execute(answer);
    }

    public void sendMessage(EditMessageText editMessageText) throws TelegramApiException {
        bot.execute(editMessageText);
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage delete = new DeleteMessage();
        delete.setChatId(String.valueOf(chatId));
        delete.setMessageId(messageId);

        try {
            bot.execute(delete);
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения: {}", e.getMessage());
        }
    }

    public void sendAnswerMessage(String text, Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());
        answer.setText(text);
        answer.setShowAlert(true);

        try {
            sendMessage(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка: {}", e.getMessage());
        }
    }

    public void removeMessage(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(String.valueOf(chatId));
            deleteMessage.setMessageId(messageId);
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.info("❌ Не удалось удалить сообщение: " + e.getMessage());
        }
    }

    public EditMessageReplyMarkup editMessage(Long chatId, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setChatId(String.valueOf(chatId));
        edit.setMessageId(messageId);
        edit.setReplyMarkup(inlineKeyboardMarkup);
        return edit;
    }

    public Integer sendInlineKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(keyboard);
        Message sentMessage = bot.execute(message);
        return sentMessage.getMessageId();
    }

    public void sendInlineKeyboard(Update update, InlineKeyboardMarkup inlineKeyboardMarkup, String text) throws TelegramApiException {
        Long chatId = utilsService.getUniversalChatId(update);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(inlineKeyboardMarkup);
        bot.execute(message);
    }

    public void sendTextMessage(String text, Update update) {
        try {
            Long chatId = utilsService.getUniversalChatId(update);
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }

    public void sendTemporaryMessage(long chatId, String text, int secondsToLive) {
        try {
            SendMessage message = new SendMessage(String.valueOf(chatId), text);
            Message sentMessage = bot.execute(message);
            int messageId = sentMessage.getMessageId();
            scheduleMessageDeletion(chatId, messageId, secondsToLive);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

    private void scheduleMessageDeletion(long chatId, int messageId, int secondsToLive) {
        new Thread(() -> {
            try {
                Thread.sleep(secondsToLive * 1000L);
                deleteMessage(chatId, messageId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }


}
