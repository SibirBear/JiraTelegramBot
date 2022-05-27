package info.fermercentr.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import info.fermercentr.config.Config;
import info.fermercentr.jiraAPI.JiraAPI;
import info.fermercentr.jiraAPI.JiraConstants;
import info.fermercentr.jiraAPI.JiraIssueURL;
import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import info.fermercentr.jiraAPI.issue.Issue;
import info.fermercentr.model.Steps;
import info.fermercentr.model.User;
import info.fermercentr.jiraAPI.CreateJiraIssue;
import info.fermercentr.service.CheckUserAuth;
import info.fermercentr.service.bot.ButtonsNameConstants;
import info.fermercentr.service.bot.SendMessageBotService;
import info.fermercentr.store.StoreOrders;
import info.fermercentr.store.StoreUsers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static info.fermercentr.core.CoreConstants.*;
import static info.fermercentr.jiraAPI.JiraIssueURL.getUrl;
import static info.fermercentr.jiraAPI.issue.GenerateStringFromList.generate;
import static info.fermercentr.service.CheckDivision.isDivisionReal;


/*
 * Класс ядра бота, основная логика
 *
 * Core bot class, main logic
 */

public class CoreBot extends TelegramLongPollingBot {

    private static final Logger LOG = LogManager.getLogger(CoreBot.class);
    private final static int DIVISION_NUMBER_LENGTH = 4;

    private final String token = Config.getConfigTelegramSettings().getToken();
    private final String botName = Config.getConfigTelegramSettings().getBotName();
    private final SendMessageBotService sendMessageBotService = new SendMessageBotService();
    private final StoreUsers storeUsers = new StoreUsers();
    private final StoreOrders storeOrders = new StoreOrders();
    private final JiraAPI jiraApi = new JiraAPI(Config.getUrlJira(),Config.getAuthJira());

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
        //checking that the user is saved in the list of users and is not "old"
        if (storeUsers.contains(userChatId)) {
            CheckUserAuth checkUserAuth = new CheckUserAuth(storeUsers.get(userChatId));
            boolean check = checkUserAuth.checkForIllegal();
            if (check) {
                executeMessage(sendMessageBotService.userCheckFailedDeleting(userChatId));
                LOG.info("[" + getClass() + "] Telegram ChatID " + userChatId + " is expired or deleted.");
                storeUsers.delete(userChatId);
            }

        }

        //если пользователя нет в списке пользователей - записываем chat ID с признаком не авторизовавшегося
        // пользователя и отправляем запрос на авторизацию (точнее сообщаем пользователю чтобы он ввел логин)
        //
        // if the user is not in the list of users, we record the chat ID with the sign of not logged in
        // user and send a request for authorization (more precisely, we inform the user to enter the login)
        if (!storeUsers.contains(userChatId)) {
            LOG.info("[" + getClass() + "] DONT CONTAINS USER " + userChatId);
            executeMessage(sendMessageBotService.authorizationLoginMessage(userChatId));
            storeUsers.save(userChatId, new User(NOT_AUTH, Steps.STEP100));
        } else {
            // Проверка на нажатие кнопки ОТМЕНА в процессе создания заявки
            // Checking for pressing the CANCEL button during the application creation process
            if (CoreConstants.CANCEL.equals(userEnteredText)) {
                storeUsers.get(userChatId).updateStep(Steps.STEP997);
            } else if (CoreConstants.DONE.equals(userEnteredText)
                    && storeUsers.get(userChatId).getStep().equals(Steps.STEP126)){
                storeUsers.get(userChatId).updateStep(Steps.STEP127);
            }

            Steps currentStep = storeUsers.get(userChatId).getStep();

            switch (currentStep) {

                //Авторизация
                //Authorization
                case STEP100:
                    //получаем текст из сообщения пользователя, предположительно логин
                    //we get the text from the user's message, presumably login
                    String loginUser = update.getMessage().getText().toLowerCase();
                    executeMessage(sendMessageBotService.awaitingMessage(userChatId));

                    LOG.info("[" + getClass() + "] " + userChatId + " STEP100 - Autorization: " + loginUser);

                    int result = jiraApi.findUserJira(loginUser);

                    LOG.info("[" + getClass() + "] " + userChatId + " STEP100 - Autorization Result: " + result);

                    if (result == HTTP_OK) {
                        storeUsers.get(userChatId).setUserName(loginUser);
                        storeUsers.get(userChatId).updateStep(Steps.STEP101);
                        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,true));
                        executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,false));
                    }
                    break;

                //Отработка кнопок меню
                //Working out the menu buttons
                case STEP101:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP101 - Main menu");

                    if (ButtonsNameConstants.CREATE_ISSUE.equals(update.getMessage().getText())) {
                        storeUsers.get(userChatId).updateStep(Steps.STEP120);
                        executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                    }

                    if (ButtonsNameConstants.LIST_ISSUES.equals(update.getMessage().getText())) {
                        storeUsers.get(userChatId).updateStep(Steps.STEP110);

                    }

                //Список заявок
                // List of issues
                case STEP110:

                    LOG.info("[" + getClass() + "] " + userChatId + " STEP110 - List of Jira issues");

                    if (storeUsers.get(userChatId).getStep().equals(Steps.STEP110)) {
                        executeMessage(sendMessageBotService.awaitingMessage(userChatId));
                        List<JiraIssueURL> jiraIssueURLList = jiraApi.listIssues(storeUsers.get(userChatId).getUserName());
                        String listIssues = generate(jiraIssueURLList);

                        storeUsers.get(userChatId).updateStep(Steps.STEP999);

                        executeMessage(sendMessageBotService.listOfIssues(userChatId, listIssues));
                        executeMessage(sendMessageBotService.listOfIssuesEnd(userChatId));

                    }

                    break;

                // Создание заявки. Выбор проекта
                // Creating issue. Choosing project
                case STEP120:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP120 - Creating issue. Choosing project.");

                    String project = JiraConstants.JIRA_PROJECT_FRANCH;
                    String issueType = JiraConstants.JIRA_ISSUE_TYPE_REGULAR;

                    if (ButtonsNameConstants.IT.equals(userEnteredText)) {
                        project = JiraConstants.JIRA_PROJECT_SUPPORT;
                    }
                    if (ButtonsNameConstants.GOODS.equals(userEnteredText)) {
                        issueType = JiraConstants.JIRA_ISSUE_TYPE_CREATE_GOODS;
                    }
                    if (ButtonsNameConstants.REPAIR.equals(userEnteredText)) {
                        issueType = JiraConstants.JIRA_ISSUE_TYPE_REPAIR;
                    }

                    storeOrders.save(userChatId, new Issue(project, storeUsers.get(userChatId).getUserName()));
                    storeOrders.get(userChatId).setIssueType(issueType);

                    storeUsers.get(userChatId).updateStep(Steps.STEP121);
                    executeMessage(sendMessageBotService.writeDivisionIssue(userChatId));

                    break;


                //Создание заявки. Выбор подразделения
                //Creating issue. Choosing department
                case STEP121:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP121 - Creating issue. Choosing department.");

                    executeMessage((sendMessageBotService.awaitingMessage(userChatId)));

                    if (userEnteredText.length() == DIVISION_NUMBER_LENGTH
                            && isDivisionReal(userEnteredText)) {
                        storeUsers.get(userChatId).updateStep(Steps.STEP122);
                        storeOrders.get(userChatId).setDepartment(userEnteredText);
                        executeMessage(sendMessageBotService.writeNameIssue(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.errorDivisionIssue(userChatId));
                    }

                    break;

                // Создание заявки. Название заявки
                // Creating issue. Naming issue
                case STEP122:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP122 - Creating issue. Naming issue.");

                    try {
                        storeOrders.get(userChatId).setNameIssue(userEnteredText);
                        storeUsers.get(userChatId).updateStep(Steps.STEP123);
                        executeMessage(sendMessageBotService.writeDescriptionIssue(userChatId));
                    } catch (JiraApiException e) {
                        executeMessage(sendMessageBotService.writeNameIssueError(userChatId));
                    }

                    break;

                // Создание заявки. Описание
                // Creating issue. Adding description
                case STEP123:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP123 - Creating issue. Adding description.");

                    try {
                        storeOrders.get(userChatId).setDescription(userEnteredText);
                        storeUsers.get(userChatId).updateStep(Steps.STEP124);
                        executeMessage(sendMessageBotService.messageUserContacts(userChatId));
                    } catch (JiraApiException e) {
                        executeMessage(sendMessageBotService.writeDescriptionIssueError(userChatId));
                    }

                    break;

                // Создание заявки. Контактные данные
                // Creating issue. Adding contact
                case STEP124:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP124 - Creating issue. Adding contact.");
                    try {
                        storeOrders.get(userChatId).setContact(userEnteredText);

                        if (Objects.equals(storeOrders.get(userChatId).getIssueType(), JiraConstants.JIRA_ISSUE_TYPE_REGULAR)) {
                            storeUsers.get(userChatId).updateStep(Steps.STEP125);
                            executeMessage(sendMessageBotService.messageEnterAnyDeskID(userChatId));
                        } else {
                            storeUsers.get(userChatId).updateStep(Steps.STEP126);
                            executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                        }
                    } catch (JiraApiException e) {
                        executeMessage(sendMessageBotService.messageUserContactsError(userChatId));
                    }

                    break;

                // Создание заявки. AnyDesk
                // Creating issue. Adding ID Anydesk
                case STEP125:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP125 - Creating issue. Adding ID Anydesk.");
                    try {
                        storeOrders.get(userChatId).setIdanydesk(userEnteredText);
                        storeUsers.get(userChatId).updateStep(Steps.STEP126);
                        executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                    } catch (JiraApiException e){
                        executeMessage(sendMessageBotService.messageWrongAnyDeskID(userChatId));

                    }

                    break;

                // Создание заявки. Прикрепление доп.файлов
                // Creating issue. Adding attachment
                case STEP126:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP126 - Creating issue. Adding attachment.");

                    String id = "1";
                    if (update.getMessage().getPhoto() != null) {
                        id = update.getMessage().getPhoto().get(TELEGRAM_PHOTO_INDEX).getFileId();
                    } else if (update.getMessage().getDocument() != null) {
                        id = update.getMessage().getDocument().getFileId();
                    }

                    GetFile getFile = new GetFile();
                    getFile.setFileId(id);

                    try {
                        org.telegram.telegrambots.meta.api.objects.File fileTG = execute(getFile);
                        String[] str = String.valueOf(fileTG.getFilePath()).split("/");
                        String fileName = Arrays.stream(str).skip(str.length - 1).findFirst().orElse("untitled");

                        LOG.info("[" + getClass() + "] " + userChatId + " STEP126 - add file: " + fileName);

                        File file = downloadFile(fileTG, new File(
                                Config.getPathToExchange()
                                + userChatId
                                + "_" + fileName)); //+ "." + ext));

                        storeOrders.get(userChatId).addAttachmentFile(file.getPath());

                    } catch (TelegramApiException e) {
                        LOG.error("[" + getClass() + "] " + "ERROR! " + userChatId
                                + " STEP126 - Creating issue. Error adding attachment: " + e.getMessage());
                    }

                    break;

                // Создание заявки. Размещение заявки в Jira
                // Creating issue. Creating issue in Jira
                case STEP127:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP127 - Creating issue in Jira...");

                    try {
                        String key = CreateJiraIssue.createJiraIssue(jiraApi, storeOrders.get(userChatId));

                        if (storeOrders.get(userChatId).getAttachment() != null) {
                            jiraApi.addMultiAttachment(key, storeOrders.get(userChatId).getAttachment());
                        }

                        LOG.info("[" + getClass() + "] " + userChatId + " STEP127 -  Issue: " + storeOrders.get(userChatId).toString());
                        executeMessage(sendMessageBotService.messageEndCreatingIssue(userChatId, getUrl(key)));

                    } catch (JiraApiException e) {
                        storeUsers.get(userChatId).updateStep(Steps.STEP997);
                        executeMessage(sendMessageBotService.messageCreatingIssueError(userChatId));
                    }

                // Отмена действия. Предупреждение
                // Cancel action. Warning
                case STEP997:
                    //TODO:
                    // Для реализации предупреждения об отмене, необходимо хранить предыдущий шаг.
                    // Это можно реализовать дополнительным аргументом в модели User,
                    // либо изменить тип хранения Steps в модели User на Dequeue (LIFO)

                // Отмена действия
                // Cancel action.
                case STEP998:
                    storeOrders.delete(userChatId);
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP998 - Deleting issue params in map...");
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP998 -  Issue: " + storeOrders.contains(userChatId));

                // Возврат в меню
                // Return to Main menu
                case STEP999:
                    LOG.info("[" + getClass() + "] " + userChatId + " STEP999 -  end action, return tu Main menu ");
                    storeUsers.get(userChatId).updateStep(Steps.STEP101);
                    executeMessage(sendMessageBotService.returnToPrimaryMenu(userChatId));
                    executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                    break;

            }
        }
    }

    //Reserve for future updates
    private void scenarioCallbackQuery(Update update) {
        long userChatId = update.getCallbackQuery().getMessage().getChatId();
        String callBackQuery = update.getCallbackQuery().getData();
        Steps currentStep = storeUsers.get(userChatId).getStep();

    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Reserve for future updates
    private void executeMessage(EditMessageText sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public StoreUsers getStoreUsers () {
        return storeUsers;
    }

    public StoreOrders getStoreOrders () {
        return storeOrders;
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
