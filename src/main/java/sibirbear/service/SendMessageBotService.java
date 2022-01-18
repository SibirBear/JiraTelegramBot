package sibirbear.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import sibirbear.store.StoreUser;

public class SendMessageBotService {

    private final ButtonBotService buttonBotService = new ButtonBotService();

/*
 * Конструкторы сообщений
 */
    private SendMessage createSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        return sendMessage;
    }

    private SendMessage createMessageWithKeyboard(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = createSimpleMessage(chatId, text);
        message.setReplyMarkup(keyboard);
        return  message;
    }


/*
 * Простые сообщения
 */
    public SendMessage startMessage(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.GREETINGS.getText());
    }

    public SendMessage authorizationMessageBefore(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.AUTH.getText());
    }

    public SendMessage checkMessage(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.CHECK.getText());
    }
    
    public SendMessage authorizationMessageAfter(long chatId, boolean result) {
        return result ? createSimpleMessage(chatId, SendMessageConstantText.AUTH_OK.getText())
                : createSimpleMessage(chatId, SendMessageConstantText.AUTH_ERROR.getText());
    }

    public SendMessage createIssueMessage(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.DESIRECREATEISSUE.getText(),
                buttonBotService.createIssueMessage());
    }

    public SendMessage chooseTypeIssueMessage(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.CHOOSETYPEISSUE.getText(),
                buttonBotService.chooseTypeIssueMessage());
    }

    //DELETE after Test
    public SendMessage abc(long chatId, StoreUser store) {
        return createSimpleMessage(chatId, store.getUser(chatId).toString());
    }

}
