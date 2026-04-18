package ru.builder.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.builder.bot.data.BotState;
import ru.builder.bot.data.TextConstants;
import ru.builder.bot.data.TypeApplication;
import ru.builder.bot.model.TimeSlotEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CallbackHandlerService {

    private final SendMessageService sendMessageService;
    private final CheckService checkService;
    private final EditMessageService editMessageService;
    private final KeyBoardService keyBoardService;
    private final TimeSlotService timeSlotService;
    private final TimeSlotHelper timeSlotHelper;
    private final UserService userService;
    private final ApplicationService applicationService;
    private final TextHandlerService textHandlerService;
    private final UtilsService utilsService;

    // Храним ID сообщений для удаления
    private final Map<Long, Integer> messagesByDelete = new ConcurrentHashMap<>();

    public CallbackHandlerService(SendMessageService sendMessageService, CheckService checkService,
                                  EditMessageService editMessageService, KeyBoardService keyBoardService,
                                  TimeSlotService timeSlotService, TimeSlotHelper timeSlotHelper,
                                  UserService userService, ApplicationService applicationService, TextHandlerService textHandlerService, UtilsService utilsService) {
        this.sendMessageService = sendMessageService;
        this.checkService = checkService;
        this.editMessageService = editMessageService;
        this.keyBoardService = keyBoardService;
        this.timeSlotService = timeSlotService;
        this.timeSlotHelper = timeSlotHelper;
        this.userService = userService;
        this.applicationService = applicationService;
        this.textHandlerService = textHandlerService;
        this.utilsService = utilsService;
    }

    public void handleAllCallback(Update update) throws TelegramApiException {
        String callbackData = update.getCallbackQuery().getData();
        switch (callbackData) {
            case "registrationForRepairs":
                applicationService.addCTypeApplication(update, TypeApplication.REPAIR);
                LocalDate now = LocalDate.now();
                int year = now.getYear();
                int month = now.getMonthValue();
                editMessageService.editAndChangeKeyboard(update, "\uD83D\uDCC5Выберите дату:", CalendarHelper.generateCalendar(year, month));
                break;
            case "registrationForMeasurement":
                applicationService.addCTypeApplication(update, TypeApplication.ZAMER);
                LocalDate now1 = LocalDate.now();
                int year1 = now1.getYear();
                int month1 = now1.getMonthValue();
                editMessageService.editAndChangeKeyboard(update, "\uD83D\uDCC5Выберите дату:", CalendarHelper.generateCalendar(year1, month1));
                break;
            case "frequentQuestions":
                editMessageService.editAndChangeKeyboard(update, "Частые воросы:", keyBoardService.createQuestionMenu());
                break;
            case "howMatchSquareMeter":
                sendMessageService.sendAnswerMessage(TextConstants.HOW_MATCH_SQUARE_METER, update);
                break;
            case "howMatchCost":
                sendMessageService.sendAnswerMessage(TextConstants.HOW_MATCH_COST, update);
                break;
            case "prepayment":
                sendMessageService.sendAnswerMessage(TextConstants.PREPAYMENT, update);
                break;
            case "clean":
                sendMessageService.sendAnswerMessage(TextConstants.CLEAN, update);
                break;
            case "typeWork":
                sendMessageService.sendAnswerMessage(TextConstants.TYPE_WORK, update);
                break;
            case "look":
                sendMessageService.sendAnswerMessage(TextConstants.LOOK, update);
                break;
            case "material":
                sendMessageService.sendAnswerMessage(TextConstants.MATERIAL, update);
                break;
            case "typeHouse":
                sendMessageService.sendAnswerMessage(TextConstants.TYPE_HOUSE, update);
                break;
            case "myApplication":
                sendMessageService.sendTemporaryMessage(utilsService.getUniversalChatId(update),
                        applicationService.getTextApplicationByUser(update), 5);
                break;
            case "close":
                editMessageService.editAndChangeKeyboard(update, "Выберите опцию:", keyBoardService.createStartMenu());
                break;
            case "back_to_main_menu":
                editMessageService.editAndChangeKeyboard(update, "Выберите опцию:", keyBoardService.createStartMenu());
            default:
                if (isCalendarCallback(callbackData)) {
                    handleCalendarCallback(update);
                } else {
                    log.warn("Неизвестный callback: {}", callbackData);
                }
                break;
        }

    }

    public void handleCalendarCallback(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        // Навигация по месяцам
        if (callbackData.startsWith("month_")) {
            String[] parts = callbackData.substring(6).split("_");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            InlineKeyboardMarkup newCalendar = CalendarHelper.generateCalendar(year, month);
            EditMessageReplyMarkup edit = sendMessageService.editMessage(chatId, messageId, newCalendar);

            try {
                sendMessageService.sendMessage(edit);
            } catch (TelegramApiException e) {
                log.error("Ошибка при смене месяца: {}", e.getMessage());
            }
        }

        // Выбор даты
        if (callbackData.startsWith("date_")) {
            String dateStr = callbackData.substring(5);
            LocalDate selectedDate = LocalDate.parse(dateStr);

            // 👇 ПРОВЕРКА: дата не должна быть в прошлом
            if (checkService.dateInThePast(selectedDate)) {
                sendMessageService.sendAnswerMessage("❌ Нельзя выбрать дату в прошлом! Выберите сегодня или позже.", update);
                return;
            }

            editMessageService.editAndChangeKeyboard(update, "Выберите свободный слот:", timeSlotHelper.generateTimeSlots(selectedDate));
        }

        // Обработка выбора времени
        if (callbackData.startsWith("time_")) {
            String[] parts = callbackData.substring(5).split("_");
            LocalDate date = LocalDate.parse(parts[0]);
            LocalTime time = LocalTime.parse(parts[1]);
            String formattedDateTime = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    " в " + time.format(DateTimeFormatter.ofPattern("HH:mm"));
            TimeSlotEntity timeSlotEntity = new TimeSlotEntity();
            timeSlotEntity.setSlotDate(date);
            timeSlotEntity.setSlotTime(time);
            timeSlotEntity.setChatId(chatId);
            timeSlotService.saveRecord(timeSlotEntity);
            applicationService.addTimeSlotId(update, timeSlotEntity);
            Integer messageInputAddress = editMessageService.editAndRemoveKeyboard(update, "Введите адрес:");
            textHandlerService.addMessageId(update, messageInputAddress);
            userService.updateUserState(update, BotState.INPUT_ADDRESS);
            log.info("Пользователь {} записался на {}", chatId, formattedDateTime);
        }

        // Обработка кнопки "Назад" — ВОЗВРАТ К КАЛЕНДАРЮ (ИСПРАВЛЕНО)
        if (callbackData.equals("back_to_calendar")) {
            // Удаляем сообщение со слотами
            Integer timeSlotMsgId = messagesByDelete.get(chatId);
            if (timeSlotMsgId != null) {
                sendMessageService.deleteMessage(chatId, timeSlotMsgId);
                messagesByDelete.remove(chatId);
            }

            // Возвращаем календарь в ЭТО ЖЕ сообщение (одна операция)
            LocalDate now = LocalDate.now();
            InlineKeyboardMarkup calendar = CalendarHelper.generateCalendar(now.getYear(), now.getMonthValue());

            // Меняем текст И кнопки в одном сообщении
            EditMessageText editText = new EditMessageText();
            editText.setChatId(String.valueOf(chatId));
            editText.setMessageId(messageId);
            editText.setText("📅 Выберите дату замера:");
            editText.setReplyMarkup(calendar);

            try {
                sendMessageService.sendMessage(editText);
            } catch (TelegramApiException e) {
                log.error("Ошибка при возврате к календарю: {}", e.getMessage());
            }
        }
    }

    private void answerCallback(CallbackQuery callbackQuery) {
        try {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            sendMessageService.sendMessage(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при подтверждении callback: {}", e.getMessage());
        }
    }

    public void addMessageForDelete(Long chatId, Integer messageId) {
        messagesByDelete.put(chatId, messageId);
    }

    private boolean isCalendarCallback(String callbackData) {
        return callbackData.startsWith("month_") ||
                callbackData.startsWith("date_") ||
                callbackData.startsWith("time_") ||
                callbackData.equals("back_to_calendar") ||
                callbackData.equals("ignore");
    }
}