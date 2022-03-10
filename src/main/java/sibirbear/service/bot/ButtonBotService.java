package sibirbear.service.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;

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
                        createRowButtons(Arrays.asList(ButtonsNameConstants.CREATE_ISSUE)),
                        createRowButtons(Arrays.asList(ButtonsNameConstants.LIST_ISSUES))));
    }

    public ReplyKeyboardMarkup chooseTypeIssueMessage() {
        return setKeyboardMessage(
                createRowButtonList(
                        createRowButtons(Arrays.asList(ButtonsNameConstants.IT, ButtonsNameConstants.ONEC)),
                        createRowButtons(Arrays.asList(ButtonsNameConstants.GOODS, ButtonsNameConstants.REPAIR))));

    }


}
