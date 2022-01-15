package sibirbear.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendMessageBotService {

    private SendMessage createSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        return sendMessage;
    }

    //заглушка
    public SendMessage mess(Update update, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(text);

        return sendMessage;
    }

    public SendMessage startMessage(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.GREETINGS.getText());
    }

    public SendMessage authorizationMessageBefore(long chatId) {
        return createSimpleMessage(chatId, SendMessageConstantText.AUTH.getText());
    }
    
    public SendMessage authorizationMessageAfter(long chatId, boolean result) {
        return result ? createSimpleMessage(chatId, SendMessageConstantText.AUTH_OK.getText())
                : createSimpleMessage(chatId, SendMessageConstantText.AUTH_ERROR.getText());
    }

}
