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
                SendMessageConstantText.DESIRE_CREATE_ISSUE.getText(),
                buttonBotService.primaryMenuButtonsMessage());
    }

    public SendMessage chooseTypeIssueMessage(long chatId) {
        SendMessage message = createMessageWithKeyboard(chatId,
                SendMessageConstantText.CHOOSE_TYPE_ISSUE.getText(),
                buttonBotService.chooseTypeIssueMessage());
        message.enableMarkdown(true);
        return message;
    }

    public SendMessage listOfIssues(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.RETURN_TO_PRIMARY_MENU_BUTTON.getText(),
                buttonBotService.returnToPrimaryMenuButtonMessage());
    }

    public SendMessage writeNameIssue(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.ENTER_ISSUE_NAME.getText());
    }

    public SendMessage messageChooseYesOrNo(long chatId, String enteredText) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.CHECK_ENTER_TEXT.getText() + enteredText,
                buttonBotService.checkEnteredTextButtonMessage());
    }

    public SendMessage writeDescriptionIssue(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_DESCRIPTION_ISSUE.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageUserContacts(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_CONTACTS.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageEnterAnyDeskID(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_ANYDESK_ID.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage returnToPrimaryMenu(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.RETURN_TO_PRIMARY_MENU_ACTION.getText());
    }

}
