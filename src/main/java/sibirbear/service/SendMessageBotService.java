package sibirbear.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SendMessageBotService {

    public SendMessage startMessage(Update update) {
        return createSimpleMessage(update, SendMessageConstantText.GREETINGS.getText());
    }

    private SendMessage createSimpleMessage(Update update, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(text);

        return sendMessage;
    }

}
