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
import sibirbear.service.bot.SendMessageBuilder;
import sibirbear.store.StoreOrders;
import sibirbear.store.StoreUsers;

import java.util.Locale;

import static sibirbear.core.CoreConstants.*;


/*
 * Класс ядра бота, основная логика
 */

public class CoreBot extends TelegramLongPollingBot {

    private final String token = Config.getConfigTelegramSettings().getToken();
    private final String botName = Config.getConfigTelegramSettings().getBotName();
    private final SendMessageBotService sendMessageBotService = new SendMessageBotService();
    private final StoreUsers storeUser = new StoreUsers();
    private final StoreOrders storeOrders = new StoreOrders();

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
        if (storeUser.get(userChatId) != null && storeUser.get(userChatId).isDateExpired()) {
            storeUser.delete(userChatId);
        }

        //если пользователя нет в списке пользователей - записываем chat ID с признаком не авторизовавшегося
        // пользователя и отправляем запрос на авторизацию (точнее сообщаем пользователю чтобы он ввел логин)
        if (!storeUser.contains(userChatId)) {
            executeMessage(sendMessageBotService.authorizationLoginMessage(userChatId));
            storeUser.save(userChatId, new User("not authorization", Steps.STEP100));
        } else {
            Steps currentStep = storeUser.get(userChatId).getStep();

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
                        storeUser.get(userChatId).setUserName(loginUser);
                        storeUser.get(userChatId).updateStep(Steps.STEP101);
                        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,true));
                        executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,false));
                    }
                    break;

                //Отработка кнопок меню
                case STEP101:
                    if (ButtonsNameConstants.CREATE_ISSUE.equals(update.getMessage().getText())) {
                        storeUser.get(userChatId).updateStep(Steps.STEP120);
                        executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                    }

                    if (ButtonsNameConstants.LIST_ISSUES.equals(update.getMessage().getText())) {
                        storeUser.get(userChatId).updateStep(Steps.STEP999);
                        executeMessage(sendMessageBotService.listOfIssues(userChatId));
                    }

                    break;

                // Отмена действия
                case STEP998:
                    storeOrders.delete(userChatId);

                // Возврат в меню
                case STEP999:
                    storeUser.get(userChatId).updateStep(Steps.STEP101);
                    executeMessage(sendMessageBotService.returnToPrimaryMenu(userChatId));
                    executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                    break;

            }
        }
    }

    private void scenarioCallbackQuery(Update update) {
        long userChatId = update.getCallbackQuery().getMessage().getChatId();
        String callBackQuery = update.getCallbackQuery().getData();
        Steps currentStep = storeUser.get(userChatId).getStep();


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
