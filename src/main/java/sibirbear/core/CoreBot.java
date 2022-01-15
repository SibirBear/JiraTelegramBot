package sibirbear.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import sibirbear.config.Config;
import sibirbear.model.Steps;
import sibirbear.model.User;
import sibirbear.service.RequestUserFromJira;
import sibirbear.service.SendMessageBotService;
import sibirbear.store.StoreUser;

import java.time.LocalDate;

import static sibirbear.core.CoreConstants.START;


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
            scenarioSteps(update);
        }
    }

    private void scenarioSteps(Update update) {
        long userChatId = update.getMessage().getChatId();
        if (!storeUser.containsUser(userChatId)) {
            executeMessage(sendMessageBotService.authtorizationMessage(userChatId));
            storeUser.saveUser(userChatId, new User(userChatId, LocalDate.now(), Steps.STEP1));
        } else {
            Steps currentStep = storeUser.getUser(userChatId).getStep();

            switch (currentStep) {
                case STEP1:
                    String loginUser = update.getMessage().getText();
                    int result = RequestUserFromJira.findUserJira(loginUser);

                    if (result == 200) {
                        executeMessage(sendMessageBotService.mess(update, "Верно!"));
                        storeUser.getUser(userChatId).updateStep(Steps.STEP2);
                    } else {
                        executeMessage(sendMessageBotService.mess(update, "ОШИБКА! Повторите ввод."));
                    }

                    break;
                case STEP2:
                    executeMessage(sendMessageBotService.mess(update, "Пока все. Кодим дальше :)"));
                    break;
            }
        }
    }

    private void executeMessage(SendMessage sendMessage) { //private <T extends BotApiMethod>
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
