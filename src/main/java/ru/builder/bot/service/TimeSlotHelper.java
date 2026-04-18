package ru.builder.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimeSlotHelper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TimeSlotService timeSlotService;

    @Autowired
    public TimeSlotHelper(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    /**
     * Генерирует inline-клавиатуру с временными слотами с 9:00 до 18:00
     *
     * @param date выбранная дата (нужна для callbackData)
     * @return InlineKeyboardMarkup с кнопками времени
     */
    public InlineKeyboardMarkup generateTimeSlots(LocalDate date) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        // Слоты с 9:00 до 18:00 включительно (10 слотов: 9,10,11,12,13,14,15,16,17,18)
        for (int hour = 9; hour <= 18; hour++) {
            if (timeSlotService.getSlotByDateAndTime(date, LocalTime.of(hour, 0)) != null) {
                continue;
            }

            if (LocalTime.of(hour, 0).isBefore(LocalTime.now()) && date.equals(LocalDate.now())) {
                continue;
            }

            LocalTime time = LocalTime.of(hour, 0);
            String timeStr = time.format(TIME_FORMATTER);

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(timeStr);
            // Формат: time_2026-04-15_10:00
            button.setCallbackData("time_" + date + "_" + timeStr);
            currentRow.add(button);

            // Каждые 3 кнопки — новая строка
            if (currentRow.size() == 3) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }

        // Добавляем последнюю строку, если не пустая
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        // Добавляем кнопку "Назад" для возврата к календарю
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("◀️ Назад к выбору даты");
        backButton.setCallbackData("back_to_calendar");
        backRow.add(backButton);
        rows.add(backRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }


    /**
     * Проверяет, доступен ли слот (с учётом текущего времени)
     */
    public static boolean isSlotAvailable(LocalDate date, LocalTime time) {
        // Проверка, что дата и время не в прошлом
        LocalDateTime selectedDateTime = LocalDateTime.of(date, time);
        LocalDateTime now = LocalDateTime.now();

        if (selectedDateTime.isBefore(now)) {
            return false; // Слот в прошлом
        }

        // TODO: проверить в БД, не занят ли уже этот слот
        return true;
    }
}
