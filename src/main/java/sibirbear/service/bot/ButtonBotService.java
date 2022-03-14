package sibirbear.service.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Arrays;
import java.util.Collections;

import static sibirbear.service.bot.ButtonBuilder.*;

/*
 * Сервис для конфигурации кнопок в сообщениях бота
 */
public class ButtonBotService {


/*
 * Подготовленные кнопки для разных сообщений
 */
    public ReplyKeyboardMarkup primaryMenuButtonsMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Collections.singletonList(ButtonsNameConstants.CREATE_ISSUE)),
                        createRowButtons(Collections.singletonList(ButtonsNameConstants.LIST_ISSUES))));
    }

    public ReplyKeyboardMarkup chooseTypeIssueMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Arrays.asList(ButtonsNameConstants.IT, ButtonsNameConstants.ONEC)),
                        createRowButtons(Arrays.asList(ButtonsNameConstants.GOODS, ButtonsNameConstants.REPAIR))));

    }

    public ReplyKeyboardMarkup checkEnteredTextButtonMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Arrays.asList(ButtonsNameConstants.NO, ButtonsNameConstants.YES))));
    }

    public ReplyKeyboardMarkup returnToPrimaryMenuButtonMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Collections.singletonList(ButtonsNameConstants.RETURNTOMENU))));
    }


}
