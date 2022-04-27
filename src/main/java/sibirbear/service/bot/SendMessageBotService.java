package sibirbear.service.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static sibirbear.service.bot.SendMessageBuilder.*;

public class SendMessageBotService {

    private final ButtonBotService buttonBotService = new ButtonBotService();

    public SendMessage startMessage(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId, SendMessageConstantText.GREETINGS.getText());
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

    public SendMessage listOfIssues(long chatId, String listIssues) {
        SendMessage message = createSimpleMessageDeleteKeyboard(chatId, listIssues);
        message.enableMarkdown(true);
        return message;
    }

    public SendMessage listOfIssuesEnd(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.RETURN_TO_PRIMARY_MENU_BUTTON.getText(),
                buttonBotService.returnToPrimaryMenuButtonMessage());
    }

    public SendMessage writeDivisionIssue(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.ENTER_DIVISION.getText());
    }

    public SendMessage errorDivisionIssue(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.ERROR_DIVISION.getText());
    }

    public SendMessage writeNameIssue(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.ENTER_ISSUE_NAME.getText());
    }

    public SendMessage writeNameIssueError(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.ENTER_ISSUE_NAME_ERROR.getText());
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

    public SendMessage writeDescriptionIssueError(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_DESCRIPTION_ISSUE_ERROR.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageUserContacts(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_CONTACTS.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageUserContactsError(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_CONTACTS_ERROR.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageEnterAnyDeskID(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ENTER_ANYDESK_ID.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageAddAttachments(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.ADD_ATTACHMENT.getText(),
                buttonBotService.addAttachmentAndCancelButton());
    }

    public SendMessage messageWrongAnyDeskID(long chatId) {
        return createMessageWithKeyboard(chatId,
                SendMessageConstantText.WRONG_ANYDESK_ID.getText(),
                buttonBotService.cancelButton());
    }

    public SendMessage messageEndCreatingIssue(long chatId, String key) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.END_CREATING_ISSUE.getText() + key);
    }

    public SendMessage messageCreatingIssueError(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.CREATING_ISSUE_ERROR.getText());
    }

    public SendMessage returnToPrimaryMenu(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.RETURN_TO_PRIMARY_MENU_ACTION.getText());
    }

}
