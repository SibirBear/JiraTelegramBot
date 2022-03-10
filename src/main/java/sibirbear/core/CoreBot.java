package sibirbear.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import sibirbear.config.Config;
import sibirbear.model.Steps;
import sibirbear.model.User;
import sibirbear.service.RequestUserFromJira;
import sibirbear.service.bot.ButtonsNameConstants;
import sibirbear.service.bot.SendMessageBotService;
import sibirbear.store.StoreUser;

import java.util.Locale;

import static sibirbear.core.CoreConstants.*;


/*
 * Класс ядра бота, основная логика
 */

public class CoreBot extends TelegramLongPollingBot {

    private final String token = Config.getConfigTelegramSettings().getToken();
    private final String botName = Config.getConfigTelegramSettings().getBotName();
    private final SendMessageBotService sendMessageBotService = new SendMessageBotService();
    private final StoreUser storeUser = new StoreUser();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (START.equals(update.getMessage().getText())) {
                executeMessage(sendMessageBotService.startMessage(update.getMessage().getChatId()));
            }
            scenarioMessageText(update);
        }

        if (update.hasCallbackQuery()) {
            scenarioCallbackQuery(update);
        }

    }

    private void scenarioMessageText(Update update) {
        long userChatId = update.getMessage().getChatId();

        //проверка, что пользователь сохранен в списке пользователей и не "старый"
        if (storeUser.getUser(userChatId) != null && storeUser.getUser(userChatId).isDateExpired()) {
            storeUser.deleteUser(userChatId);
        }

        //если пользователя нет в списке пользователей - записываем chat ID с признаком не авторизовавшегося
        // пользователя и отправляем запрос на авторизацию (точнее сообщаем пользователю чтобы он ввел логин)
        if (!storeUser.containsUser(userChatId)) {
            executeMessage(sendMessageBotService.authorizationLoginMessage(userChatId));
            storeUser.saveUser(userChatId, new User("not authorization", Steps.STEP100));
        } else {
            Steps currentStep = storeUser.getUser(userChatId).getStep();

            switch (currentStep) {

                //Авторизация
                case STEP100:

                    //получаем текст из сообщения пользователя, предположительно логин
                    String loginUser = update.getMessage().getText();
                    executeMessage(sendMessageBotService.awaitingMessage(userChatId));

                    //перенести в Jira API
                    int result = RequestUserFromJira.findUserJira(loginUser.toLowerCase(Locale.ROOT));
                    //------------------------------

                    if (result == HTTP_OK) {
                        storeUser.getUser(userChatId).setUserName(loginUser);
                        storeUser.getUser(userChatId).updateStep(Steps.STEP101);
                        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,true));
                        executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,false));
                    };
                    break;

                //Меню
                case STEP101:
                    if (ButtonsNameConstants.CREATE_ISSUE.equals(update.getMessage().getText())) {
                        storeUser.getUser(userChatId).updateStep(Steps.STEP102);
                        executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                    }

                    if (ButtonsNameConstants.LIST_ISSUES.equals(update.getMessage().getText())) {
                        storeUser.getUser(userChatId).updateStep(Steps.STEP102);
                        executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                    }

                    break;

                case STEP102:


            }
        }
    }

    private void scenarioCallbackQuery(Update update) {
        long userChatId = update.getCallbackQuery().getMessage().getChatId();
        String callBackQuery = update.getCallbackQuery().getData();
        Steps currentStep = storeUser.getUser(userChatId).getStep();


    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeMessage(EditMessageText sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

}
