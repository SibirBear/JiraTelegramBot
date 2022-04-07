package sibirbear.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import sibirbear.config.Config;
import sibirbear.jiraAPI.JiraAPI;
import sibirbear.jiraAPI.JiraConstants;
import sibirbear.jiraAPI.JiraIssueURL;
import sibirbear.model.Order;
import sibirbear.model.Steps;
import sibirbear.model.User;
import sibirbear.service.CheckDivision;
import sibirbear.service.CreateOrderJira;
import sibirbear.service.bot.ButtonsNameConstants;
import sibirbear.service.bot.SendMessageBotService;
import sibirbear.store.StoreOrders;
import sibirbear.store.StoreUsers;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private final JiraAPI jiraApi = new JiraAPI(Config.getUrlJira(),Config.getAuthJira());
    private final static int ID_ANYDESK_LENGTH = 9;
    private final static int DIVISION_NUMBER_LENGTH = 4;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) { // && update.getMessage().hasText()
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
        String userEnteredText = update.getMessage().getText();

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
            // Проверка на нажатие кнопки ОТМЕНА в процессе создания заявки
            if (CoreConstants.CANCEL.equals(userEnteredText)) {
                storeUser.get(userChatId).updateStep(Steps.STEP997);
            } else if (CoreConstants.DONE.equals(userEnteredText)
                    && storeUser.get(userChatId).getStep().equals(Steps.STEP126)){
                storeUser.get(userChatId).updateStep(Steps.STEP127);
            }

            Steps currentStep = storeUser.get(userChatId).getStep();

            switch (currentStep) {

                //Авторизация
                case STEP100:

                    //получаем текст из сообщения пользователя, предположительно логин
                    String loginUser = update.getMessage().getText();
                    executeMessage(sendMessageBotService.awaitingMessage(userChatId));

                    //перенести в Jira API
                    int result = jiraApi.findUserJira(loginUser.toLowerCase(Locale.ROOT));
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


                    //TODO Тут затык надо подумать как переход сделать
                    if (ButtonsNameConstants.LIST_ISSUES.equals(update.getMessage().getText())) {
                        storeUser.get(userChatId).updateStep(Steps.STEP110);

                    }

                    //break;

                //Список заявок
                case STEP110:
                    if (storeUser.get(userChatId).getStep().equals(Steps.STEP110)) {
                        executeMessage(sendMessageBotService.awaitingMessage(userChatId));
                        List<JiraIssueURL> j = jiraApi.listIssues(storeUser.get(userChatId).getUserName());

                        executeMessage(sendMessageBotService.listOfIssues(userChatId, j));

                        executeMessage(sendMessageBotService.listOfIssuesEnd(userChatId));
                        storeUser.get(userChatId).updateStep(Steps.STEP999);
                    }

                    break;


                // Создание заявки. Выбор проекта
                case STEP120:
                    String project = JiraConstants.PROJECT_FRANCH;
                    String issueType = JiraConstants.ISSUE_TYPE_REGULAR;

                    if (ButtonsNameConstants.IT.equals(userEnteredText)) {
                        project = JiraConstants.PROJECT_SUPPORT;
                    }
                    if (ButtonsNameConstants.GOODS.equals(userEnteredText)) {
                        issueType = JiraConstants.ISSUE_TYPE_CREATE_GOODS;
                    }
                    if (ButtonsNameConstants.REPAIR.equals(userEnteredText)) {
                        issueType = JiraConstants.ISSUE_TYPE_REPAIR;
                    }

                    storeOrders.save(userChatId, new Order(project, storeUser.get(userChatId).getUserName()));
                    storeOrders.get(userChatId).setIssueType(issueType);

                    storeUser.get(userChatId).updateStep(Steps.STEP121);
                    executeMessage(sendMessageBotService.writeDivisionIssue(userChatId));

                    break;


                //Создание заявки. Выбор подразделения
                case STEP121:
                    CheckDivision checkDivision = new CheckDivision();
                    executeMessage((sendMessageBotService.awaitingMessage(userChatId)));

                    if (userEnteredText.length() == DIVISION_NUMBER_LENGTH
                            && checkDivision.isDivisionReal(userEnteredText)) {
                        storeUser.get(userChatId).updateStep(Steps.STEP122);
                        storeOrders.get(userChatId).setDepartment(userEnteredText);
                        executeMessage(sendMessageBotService.writeNameIssue(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.errorDivisionIssue(userChatId));
                    }

                    break;

                // Создание заявки. Название заявки
                case STEP122:
                    storeOrders.get(userChatId).setNameIssue(userEnteredText);
                    storeUser.get(userChatId).updateStep(Steps.STEP123);
                    executeMessage(sendMessageBotService.writeDescriptionIssue(userChatId));

                    break;

                // Создание заявки. Описание
                case STEP123:
                    storeOrders.get(userChatId).setDescription(userEnteredText);
                    storeUser.get(userChatId).updateStep(Steps.STEP124);
                    executeMessage(sendMessageBotService.messageUserContacts(userChatId));

                    break;

                // Создание заявки. Контактные данные
                case STEP124:
                    storeOrders.get(userChatId).setContact(userEnteredText);
                    if (Objects.equals(storeOrders.get(userChatId).getIssueType(), JiraConstants.ISSUE_TYPE_REGULAR)) {
                        storeUser.get(userChatId).updateStep(Steps.STEP125);
                        executeMessage(sendMessageBotService.messageEnterAnyDeskID(userChatId));
                    } else {
                        storeUser.get(userChatId).updateStep(Steps.STEP126);
                        executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                    }

                    break;

                // Создание заявки. AnyDesk
                case STEP125:
                    if (userEnteredText.trim().length() != ID_ANYDESK_LENGTH) {
                        executeMessage(sendMessageBotService.messageWrongAnyDeskID(userChatId));
                    } else {
                        storeUser.get(userChatId).updateStep(Steps.STEP126);
                        storeOrders.get(userChatId).setIdanydesk(userEnteredText);
                        executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                    }

                    break;

                // Создание заявки. Прикрепление доп.файлов
                case STEP126:
                    // TODO: Добавить сохранение в temp директорию до загрузки, предусмотреть очистку зависших
                    System.out.println(1);
                    String id;
                    if (update.getMessage().getPhoto() != null) {
                        System.out.println(2);
                        id = update.getMessage().getPhoto().get(3).getFileId();
                        storeOrders.get(userChatId).addAttachmentFile(id);
                    } else if (update.getMessage().getDocument() != null) {
                        System.out.println(3);
                        id = update.getMessage().getDocument().getFileId();
                        storeOrders.get(userChatId).addAttachmentFile(id);
                    }
                    System.out.println(4);

                    break;

                //Создание заявки. Проверка введенных данных
                case STEP127:

                    // DELETE AFTER TEST
                            System.out.println("Creating issue...");
                            System.out.println(storeOrders.get(userChatId).isCreated());

                    CreateOrderJira.createJiraIssue(jiraApi, storeOrders.get(userChatId));

                            System.out.println(storeOrders.get(userChatId).isCreated() + "\n");

                    executeMessage(sendMessageBotService.messageEND(userChatId, storeOrders));

                    // DELETE AFTER TEST

                    //break;

                // Отмена действия. Предупреждение
                case STEP997:
                    //TODO:
                    // Для реализации предупреждения об отмене, необходимо хранить предыдущий шаг.
                    // Это можно реализовать дополнительным аргументом в модели User,
                    // либо изменить тип хранения Steps в модели User на Dequeue (LIFO)

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
