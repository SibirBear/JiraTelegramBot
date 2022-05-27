package info.fermercentr.service.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Конструкторы кнопок для добавления в сообщение
 */

public class ButtonBuilder {

/*
 * Конструкторы кнопок меню для сообщений
 */

    //Создание одного ряда с кнопками
    public static KeyboardRow createRowButtons(List<String> buttonsName) {
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String buttonName : buttonsName) {
            keyboardRow.add(new KeyboardButton(buttonName));
        }

        return keyboardRow;
    }

    //Создание списка с рядами кнопок
    public static List<KeyboardRow> createRowButtonList(KeyboardRow... keyboardRows) {
        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        Collections.addAll(keyboardRowsList, keyboardRows);

        return keyboardRowsList;
    }

    //Создание клавиатуры для сообщения из списка с рядами кнопок
    public static ReplyKeyboardMarkup setKeyboardMessage(List<KeyboardRow> keyboardRow) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRow);
        return replyKeyboardMarkup;
    }

/*
 * Конструкторы встраиваемых в сообщения кнопок
 */

    //Конструктор встраиваемой кнопки
    public static InlineKeyboardButton createInlineButton(String buttonText, String callback) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(callback);

        return inlineKeyboardButton;
    }

    //Конструктор рядов встраиваимых кнопок
    public static List<InlineKeyboardButton> createInlineRowButton(InlineKeyboardButton... inlineButtons) {
        List<InlineKeyboardButton> inlineKeyboardButtonList = new ArrayList<>();
        Collections.addAll(inlineKeyboardButtonList, inlineButtons);

        return inlineKeyboardButtonList;
    }

    //Конструктор встраиваемой клавиатуры из рядов кнопок
    @SafeVarargs
    public static InlineKeyboardMarkup createInlineKeyboard(List<InlineKeyboardButton>... rowButtonsList) {
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();
        Collections.addAll(inlineKeyboard, rowButtonsList);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);

        return inlineKeyboardMarkup;
    }

}
