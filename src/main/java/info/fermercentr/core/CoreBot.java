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
import info.fermercentr.jiraAPI.CreateJiraIssue;
import info.fermercentr.model.Steps;
import info.fermercentr.model.User;
import info.fermercentr.service.CheckUserAuth;
import info.fermercentr.service.bot.ButtonsNameConstants;
import info.fermercentr.service.bot.SendMessageBotService;
import info.fermercentr.store.StoreOrders;
import info.fermercentr.store.StoreUsers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static info.fermercentr.core.CoreConstants.HTTP_OK;
import static info.fermercentr.core.CoreConstants.NOT_AUTH;
import static info.fermercentr.core.CoreConstants.START;
import static info.fermercentr.core.CoreConstants.TELEGRAM_PHOTO_INDEX;
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

    public CoreBot() throws JiraApiException {
    }

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

        //если пользователя нет в списке пользователей - записываем chat ID с признаком не авторизовавшегося
        // пользователя и отправляем запрос на авторизацию (точнее сообщаем пользователю чтобы он ввел логин)
        //
        // if the user is not in the list of users, we record the chat ID with the sign of not logged in
        // user and send a request for authorization (more precisely, we inform the user to enter the login)
        if (!storeUsers.contains(userChatId)) {
            LOG.info("[" + getClass() + "] DON`T CONTAINS USER " + userChatId + ". Created.");
            storeUsers.save(userChatId, new User(NOT_AUTH, Steps.STEP100));
        }

        //проверка, что пользователь сохранен в списке пользователей и не "старый"
        //checking that the user is saved in the list of users and is not "old"
        if (storeUsers.contains(userChatId)) {
            CheckUserAuth checkUserAuth = new CheckUserAuth(storeUsers.get(userChatId));
            boolean check = checkUserAuth.checkForIllegal();
            if (check) {
                executeMessage(sendMessageBotService.userCheckFailedDeleting(userChatId));
                LOG.info("[" + getClass() + "] Telegram ChatID " + userChatId + " is expired or deleted.");
                storeUsers.delete(userChatId);
                storeUsers.save(userChatId, new User(NOT_AUTH, Steps.STEP100));
            } else {
                // Проверка на нажатие кнопки ОТМЕНА в процессе создания заявки
                // Checking for pressing the CANCEL button during the application creation process
                if (CoreConstants.CANCEL.equals(userEnteredText)) {
                    storeUsers.get(userChatId).updateStep(Steps.STEP997);
                } else if (CoreConstants.DONE.equals(userEnteredText)
                        && storeUsers.get(userChatId).getStep().equals(Steps.STEP126)) {
                    storeUsers.get(userChatId).updateStep(Steps.STEP127);
                } else if (CoreConstants.LOGOUT.equalsIgnoreCase(userEnteredText)) {
                    executeMessage(sendMessageBotService.userLogOut(userChatId));
                    LOG.info("[" + getClass() + "] Telegram ChatID " + userChatId + " is logout.");
                    storeUsers.delete(userChatId);
                    storeUsers.save(userChatId, new User(NOT_AUTH, Steps.STEP100));
                }
            }

        }

        Steps currentStep = storeUsers.get(userChatId).getStep();

        switch (currentStep) {

            //Запрос на ввод логина
            case STEP100:
                LOG.info("[" + getClass() + "] " + userChatId
                        + " " + Steps.STEP100 + " - Start scenario steps.");
                executeMessage(sendMessageBotService.authorizationLoginMessage(userChatId));
                storeUsers.get(userChatId).updateStep(Steps.STEP101);
                break;

            //Авторизация
            //Authorization
            case STEP101:
                //получаем текст из сообщения пользователя, предположительно логин
                //we get the text from the user's message, presumably login
                LOG.info("[" + getClass() + "] " + userChatId
                        + " " + Steps.STEP101 + " - Authorization.");
                //if (!checkUserAndOrder(userChatId, Steps.STEP101)) break;

                String loginUser = update.getMessage().getText().toLowerCase();
                executeMessage(sendMessageBotService.awaitingMessage(userChatId));

                LOG.info("[" + getClass() + "] " + userChatId
                        + " " + Steps.STEP101 + " - Authorization: " + loginUser);

                int result;
                try {
                    result = jiraApi.findUserJira(loginUser);
                } catch (JiraApiException e) {
                    LOG.error("[" + getClass() + "] " + "ERROR! " + userChatId
                            + " " + Steps.STEP101 + " - Authorization Connecting: " + e.getMessage());
                    executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,false));
                    break;
                }

                LOG.info("[" + getClass() + "] " + userChatId + " "
                        + Steps.STEP101 + "  - Authorization Result: " + result);

                if (result == HTTP_OK) {
                    storeUsers.get(userChatId).setUserName(loginUser);
                    storeUsers.get(userChatId).updateStep(Steps.STEP102);
                    executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,true));
                    executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                } else {
                    executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,false));
                }
                break;

            //Отработка кнопок меню
            //Working out the menu buttons
            case STEP102:
                LOG.info("[" + getClass() + "] " + userChatId
                        + " " + Steps.STEP102 + " - Main menu");
                if (!checkUser(userChatId, Steps.STEP102)) break;

                if (ButtonsNameConstants.CREATE_ISSUE.equals(update.getMessage().getText())) {
                    storeUsers.get(userChatId).updateStep(Steps.STEP120);
                    executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                }

                if (ButtonsNameConstants.HELP.equals(update.getMessage().getText())) {
                    executeMessage(sendMessageBotService.helpMessage(userChatId, Config.getHelpVideoURL()));
                    executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                }

                if (ButtonsNameConstants.LIST_ISSUES.equals(update.getMessage().getText())) {
                    storeUsers.get(userChatId).updateStep(Steps.STEP110);
                }

            //Список заявок
            // List of issues
            case STEP110:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP110 - List of Jira issues");
                if (!checkUser(userChatId, Steps.STEP110)) break;

                if (storeUsers.get(userChatId).getStep().equals(Steps.STEP110)) {
                    executeMessage(sendMessageBotService.awaitingMessage(userChatId));

                    try {
                        List<JiraIssueURL> jiraIssueURLList = jiraApi.listIssues(storeUsers.get(userChatId).getUserName());
                        String listIssues = generate(jiraIssueURLList);

                        storeUsers.get(userChatId).updateStep(Steps.STEP999);

                        executeMessage(sendMessageBotService.listOfIssues(userChatId, listIssues));
                        executeMessage(sendMessageBotService.listOfIssuesEnd(userChatId));
                    } catch (JiraApiException e) {
                        LOG.error("[" + getClass() + "] " + "ERROR! " + userChatId
                                + " STEP110 - List of Jira issues: " + e.getMessage());
                        executeMessage(sendMessageBotService.listOfIssuesEnd(userChatId));
                        break;
                    }

                }

                break;

            // Создание заявки. Выбор проекта
            // Creating issue. Choosing project
            case STEP120:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP120 - Creating issue. Choosing project.");
                if (!checkUser(userChatId, Steps.STEP120)) break;

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

                try {
                    storeOrders.save(userChatId, new Issue(project, storeUsers.get(userChatId).getUserName()));
                    storeOrders.get(userChatId).setIssueType(issueType);

                    storeUsers.get(userChatId).updateStep(Steps.STEP121);

                    executeMessage(sendMessageBotService.writeDivisionIssue(userChatId));
                } catch (JiraApiException e) {
                    LOG.error("[" + getClass() + "] " + userChatId
                            + " ERROR! STEP122 - Creating issue. Naming issue. " + e.getMessage());
                    executeMessage(sendMessageBotService.errorNullProjectOrReporter(userChatId));
                    executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                }

                break;


            //Создание заявки. Выбор подразделения
            //Creating issue. Choosing department
            case STEP121:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP121 - Creating issue. Choosing department.");

                executeMessage((sendMessageBotService.awaitingMessage(userChatId)));
                if (!checkUserAndOrder(userChatId, Steps.STEP121)) break;

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
                if (!checkUserAndOrder(userChatId, Steps.STEP122)) break;

                try {
                    storeOrders.get(userChatId).setNameIssue(userEnteredText);
                    storeUsers.get(userChatId).updateStep(Steps.STEP123);
                    executeMessage(sendMessageBotService.writeDescriptionIssue(userChatId));
                } catch (JiraApiException e) {
                    LOG.error("[" + getClass() + "] " + userChatId
                            + " ERROR! STEP122 - Creating issue. Naming issue. " + e.getMessage());
                    executeMessage(sendMessageBotService.writeNameIssueError(userChatId));
                }

                break;

            // Создание заявки. Описание
            // Creating issue. Adding description
            case STEP123:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP123 - Creating issue. Adding description.");
                if (!checkUserAndOrder(userChatId, Steps.STEP123)) break;

                try {
                    storeOrders.get(userChatId).setDescription(userEnteredText);
                    storeUsers.get(userChatId).updateStep(Steps.STEP124);
                    executeMessage(sendMessageBotService.messageUserContacts(userChatId));
                } catch (JiraApiException e ) {
                    LOG.error("[" + getClass() + "] " + userChatId
                            + " ERROR! STEP123 - Creating issue. Adding description. " + e.getMessage());
                    executeMessage(sendMessageBotService.writeDescriptionIssueError(userChatId));
                }

                break;

            // Создание заявки. Контактные данные
            // Creating issue. Adding contact
            case STEP124:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP124 - Creating issue. Adding contact.");
                if (!checkUserAndOrder(userChatId, Steps.STEP124)) break;

                try {
                    storeOrders.get(userChatId).setContact(userEnteredText);
                } catch (JiraApiException e) {
                    LOG.error("[" + getClass() + "] " + userChatId
                            + " ERROR! STEP124 - Creating issue. Adding contact. " + e.getMessage());
                    executeMessage(sendMessageBotService.messageUserContactsError(userChatId));
                    break;
                }

                if (Objects.equals(storeOrders.get(userChatId).getIssueType(), JiraConstants.JIRA_ISSUE_TYPE_REGULAR)) {
                    storeUsers.get(userChatId).updateStep(Steps.STEP125);
                    executeMessage(sendMessageBotService.messageEnterAnyDeskID(userChatId));
                } else {
                    storeUsers.get(userChatId).updateStep(Steps.STEP126);
                    executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                }

                break;

            // Создание заявки. AnyDesk
            // Creating issue. Adding ID Anydesk
            case STEP125:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP125 - Creating issue. Adding ID Anydesk.");
                if (!checkUserAndOrder(userChatId, Steps.STEP125)) break;

                try {
                    storeOrders.get(userChatId).setIdanydesk(userEnteredText);
                } catch (JiraApiException e) {
                    LOG.error("[" + getClass() + "] " + userChatId
                            + " ERROR! STEP125 - Creating issue. Adding ID Anydesk. " + e.getMessage());
                    executeMessage(sendMessageBotService.messageWrongAnyDeskID(userChatId));
                    break;
                }
                storeUsers.get(userChatId).updateStep(Steps.STEP126);
                executeMessage(sendMessageBotService.messageAddAttachments(userChatId));

                break;

            // Создание заявки. Прикрепление доп.файлов
            // Creating issue. Adding attachment
            case STEP126:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP126 - Creating issue. Adding attachment.");

                if (!checkUserAndOrder(userChatId, Steps.STEP126)) break;

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
                            + "_" + fileName));

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
                executeMessage(sendMessageBotService.awaitingMessage(userChatId));
                if (!checkUserAndOrder(userChatId, Steps.STEP127)) break;

                try {
                    String key = CreateJiraIssue.createJiraIssue(jiraApi, storeOrders.get(userChatId));

                    if (storeOrders.get(userChatId).getAttachment() != null) {
                        jiraApi.addMultiAttachment(key, storeOrders.get(userChatId).getAttachment());
                    }

                    LOG.info("[" + getClass() + "] " + userChatId + " STEP127 - Issue: " + storeOrders.get(userChatId).toString());
                    executeMessage(sendMessageBotService.messageEndCreatingIssue(userChatId, getUrl(key)));

                } catch (JiraApiException e) {
                    LOG.error("[" + getClass() + "] " + userChatId + " STEP127 - Issue: " + e);
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
                LOG.info("[" + getClass() + "] " + userChatId + " STEP998 - Issue: " + storeOrders.contains(userChatId));

            // Возврат в меню
            // Return to Main menu
            case STEP999:
                LOG.info("[" + getClass() + "] " + userChatId + " STEP999 - end action, return to Main menu ");
                if (!checkUserAndOrder(userChatId, Steps.STEP999)) break;

                storeUsers.get(userChatId).updateStep(Steps.STEP102);
                executeMessage(sendMessageBotService.returnToPrimaryMenu(userChatId));
                executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));
                break;

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

    private boolean checkUser(long userChatId, Steps step) {
        if (storeUsers.contains(userChatId)) {
            return true;
        }

        LOG.error("[" + getClass() + "] " + "ERROR! No such USER in store: " + userChatId
                + " on step " + step);
        executeMessage(sendMessageBotService.authorizationLoginResultMessage(userChatId,false));
        return false;

    }

    private boolean checkUserAndOrder(long userChatId, Steps step) {
        if (checkUser(userChatId, step) && storeOrders.contains(userChatId)) {
            return true;
        }

        LOG.error("[" + getClass() + "] " + "ERROR! No saved ORDER in store: " + userChatId
                + " on step " + step + ". Return to main menu.");
        storeUsers.get(userChatId).updateStep(Steps.STEP102);
        executeMessage(sendMessageBotService.missOrderMessage(userChatId));
        executeMessage(sendMessageBotService.primaryMenuMessage(userChatId));

        return false;

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
