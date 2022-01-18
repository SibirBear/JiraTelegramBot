package sibirbear.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Сервис для конфигурации кнопок в сообщениях бота
 */
public class ButtonBotService {

/*
 * Конструкторы кнопок в сообщениях
 */

    //Создание одного ряда с кнопками
    public KeyboardRow createRowButtons(List<String> buttonsName) {
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String buttonName : buttonsName) {
            keyboardRow.add(new KeyboardButton(buttonName));
        }

        return keyboardRow;
    }

    //Создание списка с рядами кнопок
    public List<KeyboardRow> createRowButtonList(KeyboardRow... keyboardRows) {
        List<KeyboardRow> keyboardRowsList = new ArrayList<>();
        Collections.addAll(keyboardRowsList, keyboardRows);

        return keyboardRowsList;
    }

    //Создание клавиатуры для сообщения из списка с рядами кнопок
    public ReplyKeyboardMarkup setKeyboardMessage(List<KeyboardRow> keyboardRow) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(keyboardRow);

        return replyKeyboardMarkup;
    }

/*
 * Подготовленные кнопки для разных сообщений
 */
    public ReplyKeyboardMarkup createIssueMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Arrays.asList(ButtonsNameConstants.CREATE_ISSUE))));
    }

    public ReplyKeyboardMarkup chooseTypeIssueMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Arrays.asList(ButtonsNameConstants.IT, ButtonsNameConstants.ONEC)),
                        createRowButtons(Arrays.asList(ButtonsNameConstants.GOODS, ButtonsNameConstants.REPAIR))));


    }

}
