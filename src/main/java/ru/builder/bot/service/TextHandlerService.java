package ru.builder.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.builder.bot.data.BotState;

import java.util.HashMap;

@Slf4j
@Service
public class TextHandlerService {

    private final SendMessageService sendMessageService;
    private final CheckService checkService;
    private final ApplicationService applicationService;
    private final UtilsService utilsService;
    private final KeyBoardService keyBoardService;
    private final UserService userService;
    private final HashMap<Long, Integer> usersMessageByDelete = new HashMap<>();

    @Autowired
    public TextHandlerService(SendMessageService sendMessageService, CheckService checkService, ApplicationService applicationService, UtilsService utilsService, KeyBoardService keyBoardService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.checkService = checkService;
        this.applicationService = applicationService;
        this.utilsService = utilsService;
        this.keyBoardService = keyBoardService;
        this.userService = userService;
    }

    public void handleText(Update update) throws TelegramApiException {
        if (checkService.messageIsCommandStart(update)) {
            long chatId = utilsService.getUniversalChatId(update);
            int userMessageId = update.getMessage().getMessageId();
            sendMessageService.deleteMessage(chatId, userMessageId);
            if (userService.getUserByChatId(update) == null) {
                log.info("новый пользователь");
                userService.saveUser(update, BotState.NEW);
            }
            userService.updateUserState(update, BotState.NEW);
            sendMessageService.sendInlineKeyboard(chatId, "Выберите опцию:", keyBoardService.createStartMenu());
        }

        if (checkService.botStateIs(update, BotState.INPUT_ADDRESS)) {
            Long chatId = utilsService.getUniversalChatId(update);
            String message = utilsService.getUniversalMessage(update);
            int messageId = update.getMessage().getMessageId();
            applicationService.addAddress(update, message);
            String doneMessage = applicationService.saveApplication(update);
            sendMessageService.deleteMessage(chatId, usersMessageByDelete.get(chatId));
            usersMessageByDelete.remove(chatId);
            sendMessageService.deleteMessage(chatId, messageId);;
            sendMessageService.sendTemporaryMessage(chatId, "✅Ваша заявка успешно создана и направлена специалисту!", 5);
            sendMessageService.sendTemporaryMessage(chatId, doneMessage, 5);
            userService.updateUserState(update, BotState.NEW);
        }
    }

    public void addMessageId(Update update, Integer messageId) {
        Long chatId = utilsService.getUniversalChatId(update);
        usersMessageByDelete.put(chatId, messageId);
    }
}
