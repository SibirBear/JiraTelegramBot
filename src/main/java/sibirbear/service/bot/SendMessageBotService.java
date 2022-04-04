package sibirbear.service.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import sibirbear.jiraAPI.exceptions.JiraIssueURL;
import sibirbear.store.StoreOrders;

import java.util.List;

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

    public SendMessage listOfIssues(long chatId, List<JiraIssueURL> listIssues) {

        //TODO Убрать отсюда в отдельный класс
        StringBuilder sb = new StringBuilder();
        sb.append("*Список заявок:*\n\n");

        if (listIssues.size() == 0) {
            sb.append("_Открытых заявок, созданных тобой - нет._\n");
        } else {
            for (JiraIssueURL jiraIssueURL : listIssues) {
                String url = "*" + jiraIssueURL.getUrl() + "*\n";
                String description = "_" + jiraIssueURL.getDescription() + "_\n\n";
                sb.append(url);
                sb.append(description);
            }
        }

        SendMessage message = createSimpleMessageDeleteKeyboard(chatId, sb.toString());
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

    public SendMessage messageEND(long chatId, StoreOrders storeOrders) {
        return createMessageWithKeyboard(chatId,
                storeOrders.get(chatId).toString(),
                buttonBotService.cancelButton());
    }

    public SendMessage returnToPrimaryMenu(long chatId) {
        return createSimpleMessageDeleteKeyboard(chatId,
                SendMessageConstantText.RETURN_TO_PRIMARY_MENU_ACTION.getText());
    }

}
