package info.fermercentr.service.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;
import java.util.Collections;

import static info.fermercentr.service.bot.ButtonBuilder.*;

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

    public ReplyKeyboardMarkup cancelButton() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Collections.singletonList(ButtonsNameConstants.CANCEL))));
    }

    public ReplyKeyboardMarkup addAttachmentAndCancelButton() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Arrays.asList(ButtonsNameConstants.DONE, ButtonsNameConstants.CANCEL))));
    }

    public ReplyKeyboardMarkup returnToPrimaryMenuButtonMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Collections.singletonList(ButtonsNameConstants.RETURNTOMENU))));
    }


}
