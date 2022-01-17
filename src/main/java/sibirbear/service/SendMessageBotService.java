package sibirbear.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static java.lang.Math.toIntExact;

public class SendMessageBotService {

    private final ButtonBotService buttonBotService = new ButtonBotService();

    private SendMessage createSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        return sendMessage;
    }

    private EditMessageText createSimpleEditMessage(Update update, String answer) {
        EditMessageText editMessageText = new EditMessageText();
        long mesId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        editMessageText.setMessageId(toIntExact(mesId));
        editMessageText.setChatId(String.valueOf(chatId));
        //editMessageText.enableMarkdown(true); //для стилизации
        editMessageText.setText(answer);

        return editMessageText;
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

    public SendMessage desireCreateRequestJira(long chatId) {
        SendMessage sendMessage = createSimpleMessage(chatId,SendMessageConstantText.DESIRECREATEREQUEST.getText());
        InlineKeyboardMarkup replyKeyboardMarkup =
                buttonBotService.setInlineKeyboard(buttonBotService.
                        createInlineButton(ButtonBotServiceTitle.YES.getTitle()));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public EditMessageText desireCreateRequestJiraAnswer(Update update) {
        return createSimpleEditMessage(update, SendMessageConstantText.DESIRECREATEREQUEST.getText()
                + "\n" + ButtonBotServiceTitle.YES.getTitle());
    }

    public SendMessage message(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        return sendMessage;
    }

}
