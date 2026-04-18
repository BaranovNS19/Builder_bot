package ru.builder.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyBoardService {

    public InlineKeyboardMarkup createStartMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Записаться на ремонт объкта \uD83D\uDEE0\uFE0F\uD83D\uDCDD\uD83D\uDCCD",
                "registrationForRepairs"));
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Записаться на замер/консультацию \uD83D\uDCD0\uD83D\uDCDD",
                "registrationForMeasurement"));
        rows.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("Частые вопросы \uD83D\uDCAC❓",
                "frequentQuestions"));
        rows.add(row3);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(createButton("Мои заявки \uD83D\uDCCB\uD83D\uDC64",
                "myApplication"));
        rows.add(row4);

        markup.setKeyboard(rows);
        return markup;
    }


    public InlineKeyboardMarkup createQuestionMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Сколько стоит м2 под ключ \uD83D\uDCCF\uD83D\uDCB0?",
                "howMatchSquareMeter"));
        rows.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Сколько стоит выезд замерщика? \uD83D\uDCCF\uD83D\uDE97\uD83D\uDCB5",
                "howMatchCost"));
        rows.add(row2);

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("Есть ли предоплата? \uD83D\uDCB8\uD83E\uDD37",
                "prepayment"));
        rows.add(row3);

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(createButton("Вы убираете после ремонта мусор? \uD83D\uDD28\uD83E\uDDF9❓",
                "clean"));
        rows.add(row4);

        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(createButton("Какие типы работ выполняете? \uD83D\uDEE0\uFE0F❓",
                "typeWork"));
        rows.add(row5);

        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(createButton("Можно приезжать смотреть процесс? \uD83D\uDD28\uD83D\uDC40",
                "look"));
        rows.add(row6);

        List<InlineKeyboardButton> row7 = new ArrayList<>();
        row7.add(createButton("Вы помогаете купить материалы? \uD83D\uDED2❓",
                "material"));
        rows.add(row7);

        List<InlineKeyboardButton> row8 = new ArrayList<>();
        row8.add(createButton("Какими типами жилья занимаетесь? \uD83C\uDFE0\uD83C\uDFE2❓",
                "typeHouse"));
        rows.add(row8);

        List<InlineKeyboardButton> row9 = new ArrayList<>();
        row9.add(createButton("Закрыть ❌",
                "close"));
        rows.add(row9);

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
