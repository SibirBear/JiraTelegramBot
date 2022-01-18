package sibirbear.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import sibirbear.config.Config;
import sibirbear.model.Steps;
import sibirbear.model.User;
import sibirbear.service.ButtonsNameConstants;
import sibirbear.service.RequestUserFromJira;
import sibirbear.service.SendMessageBotService;
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


        if (storeUser.getUser(userChatId) != null && storeUser.getUser(userChatId).isDateExpired()) {
            storeUser.deleteUser(userChatId);
        }

        if (!storeUser.containsUser(userChatId)) {
            executeMessage(sendMessageBotService.authorizationMessageBefore(userChatId));
            storeUser.saveUser(userChatId, new User("not authorization", Steps.STEP1));
        } else {
            Steps currentStep = storeUser.getUser(userChatId).getStep();

            switch (currentStep) {

                //Авторизация
                case STEP1:
                    String loginUser = update.getMessage().getText();
                    executeMessage(sendMessageBotService.checkMessage(userChatId));
                    int result = RequestUserFromJira.findUserJira(loginUser.toLowerCase(Locale.ROOT));

                    if (result == HTTP_OK) {
                        executeMessage(sendMessageBotService.authorizationMessageAfter(userChatId,true));
                        storeUser.getUser(userChatId).setUserName(loginUser);
                        storeUser.getUser(userChatId).updateStep(Steps.STEP2);

                        //DELETE after test
                        executeMessage(sendMessageBotService.abc(userChatId, storeUser));
                        //DELETE after test

                        executeMessage(sendMessageBotService.createIssueMessage(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.authorizationMessageAfter(userChatId,false));
                    }
                    break;

                //Запрос создания заявки
                case STEP2:
                    if (ButtonsNameConstants.CREATE_ISSUE.equals(update.getMessage().getText())) {
                        executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                    }

                    break;

                case STEP3:

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

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

}
