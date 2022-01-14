package sibirbear.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/*
 * Сервис для конфигурации кнопок в сообщениях бота
 */

public class ButtonService {

    public List<List<InlineKeyboardButton>> createInlineButton (String buttonName) {
        List<List<InlineKeyboardButton>> inlineKeyboardButtonList= new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton =new InlineKeyboardButton();

        inlineKeyboardButton.setText(buttonName);
        inlineKeyboardButton.setCallbackData(buttonName);

        inlineKeyboardButtonsRow.add(inlineKeyboardButton);

        inlineKeyboardButtonList.add(inlineKeyboardButtonsRow);

        return inlineKeyboardButtonList;
    }

    public InlineKeyboardMarkup setInlineKeyboard(List<List<InlineKeyboardButton>> inlineList) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        inlineKeyboardMarkup.setKeyboard(inlineList);

        return inlineKeyboardMarkup;
    }

}
