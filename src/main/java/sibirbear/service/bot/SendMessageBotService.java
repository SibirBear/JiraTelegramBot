package sibirbear.service.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static sibirbear.service.bot.SendMessageBuilder.*;

public class SendMessageBotService {

    private final ButtonBotService buttonBotService = new ButtonBotService();

    public SendMessage startMessage(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.GREETINGS.getText());
    }

    public SendMessage authorizationLoginMessage(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.AUTH.getText());
    }

    public SendMessage awaitingMessage(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.AWAITING.getText());
    }

    public SendMessage authorizationLoginResultMessage(long chatId, boolean result) {
        return result ? createSimpleMessage(chatId, SendMessageConstantText.AUTH_OK.getText())
                : createSimpleMessage(chatId, SendMessageConstantText.AUTH_ERROR.getText());
    }

    public SendMessage primaryMenuMessage(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.DESIRECREATEISSUE.getText(),
                buttonBotService.primaryMenuButtonsMessage());
    }

    public SendMessage chooseTypeIssueMessage(long chatId) {
        SendMessage message = createMessageWithKeyboard(chatId,
                SendMessageConstantText.CHOOSETYPEISSUE.getText(),
                buttonBotService.chooseTypeIssueMessage());
        message.enableMarkdown(true);
        return message;
    }



    //ReplyKeyboardRemove rkm = new ReplyKeyboardRemove(true);

}