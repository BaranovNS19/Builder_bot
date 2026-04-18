package ru.builder.bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

public class CalendarHelper {

    public static InlineKeyboardMarkup generateCalendar(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstOfMonth = yearMonth.atDay(1);

        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int emptyCells = firstDayOfWeek - 1;
        int daysInMonth = yearMonth.lengthOfMonth();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createHeaderRow(year, month));
        rows.add(createWeekDaysRow());

        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (int i = 0; i < emptyCells; i++) {
            currentRow.add(createEmptyButton());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            InlineKeyboardButton dayButton = new InlineKeyboardButton();
            dayButton.setText(String.valueOf(day));
            dayButton.setCallbackData("date_" + date);
            currentRow.add(dayButton);

            if (currentRow.size() == 7) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }

        if (!currentRow.isEmpty()) {
            while (currentRow.size() < 7) {
                currentRow.add(createEmptyButton());
            }
            rows.add(currentRow);
        }

        rows.add(createNavigationRow(year, month));
        rows.add(createBackButtonRow());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private static List<InlineKeyboardButton> createHeaderRow(int year, int month) {
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        InlineKeyboardButton header = new InlineKeyboardButton();
        header.setText(monthName + " " + year);
        header.setCallbackData("ignore");
        return Collections.singletonList(header);
    }

    private static List<InlineKeyboardButton> createWeekDaysRow() {
        String[] weekDays = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String day : weekDays) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(day);
            btn.setCallbackData("ignore");
            row.add(btn);
        }
        return row;
    }

    private static InlineKeyboardButton createEmptyButton() {
        InlineKeyboardButton empty = new InlineKeyboardButton();
        empty.setText(" ");
        empty.setCallbackData("ignore");
        return empty;
    }

    private static List<InlineKeyboardButton> createNavigationRow(int year, int month) {
        List<InlineKeyboardButton> row = new ArrayList<>();

        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth < 1) {
            prevMonth = 12;
            prevYear = year - 1;
        }

        int nextMonth = month + 1;
        int nextYear = year;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextYear = year + 1;
        }

        InlineKeyboardButton prevButton = new InlineKeyboardButton();
        prevButton.setText("◀ " + getShortMonthName(prevMonth));
        prevButton.setCallbackData("month_" + prevYear + "_" + prevMonth);

        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText(getShortMonthName(nextMonth) + " ▶");
        nextButton.setCallbackData("month_" + nextYear + "_" + nextMonth);

        row.add(prevButton);
        row.add(nextButton);

        return row;
    }

    private static String getShortMonthName(int month) {
        return Month.of(month).getDisplayName(TextStyle.SHORT_STANDALONE, new Locale("ru"));
    }

    private static List<InlineKeyboardButton> createBackButtonRow() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("◀️ Назад в главное меню");
        backButton.setCallbackData("back_to_main_menu");
        row.add(backButton);
        return row;
    }
}
