package sibirbear.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import sibirbear.config.Config;
import sibirbear.jiraAPI.JiraAPI;
import sibirbear.jiraAPI.JiraConstants;
import sibirbear.jiraAPI.JiraIssueURL;
import sibirbear.jiraAPI.exceptions.JiraApiException;
import sibirbear.jiraAPI.issue.Issue;
import sibirbear.model.Steps;
import sibirbear.model.User;
import sibirbear.service.CheckDivision;
import sibirbear.jiraAPI.CreateJiraIssue;
import sibirbear.service.bot.ButtonsNameConstants;
import sibirbear.service.bot.SendMessageBotService;
import sibirbear.store.StoreOrders;
import sibirbear.store.StoreUsers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static sibirbear.core.CoreConstants.*;
import static sibirbear.jiraAPI.JiraIssueURL.getUrl;
import static sibirbear.jiraAPI.issue.GenerateStringFromList.generate;
import static sibirbear.service.CheckDivision.isDivisionReal;


/*
 * Класс ядра бота, основная логика
 */

public class CoreBot extends TelegramLongPollingBot {

    private static final Logger log = LogManager.getLogger(CoreBot.class);
    private final String token = Config.getConfigTelegramSettings().getToken();
    private final String botName = Config.getConfigTelegramSettings().getBotName();
    private final SendMessageBotService sendMessageBotService = new SendMessageBotService();
    private final StoreUsers storeUser = new StoreUsers();
    private final StoreOrders storeOrders = new StoreOrders();
    private final JiraAPI jiraApi = new JiraAPI(Config.getUrlJira(),Config.getAuthJira());
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

        System.out.println(userChatId);

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
                    String loginUser = update.getMessage().getText().toLowerCase();
                    executeMessage(sendMessageBotService.awaitingMessage(userChatId));

                    log.info("[" + getClass() + "] " + userChatId + " STEP100 - Autorization: " + loginUser);

                    //перенести в Jira API
                    int result = jiraApi.findUserJira(loginUser);
                    //------------------------------

                    log.info("[" + getClass() + "] " + userChatId + " STEP100 - Autorization Result: " + result);

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
                    log.info("[" + getClass() + "] " + userChatId + " STEP101 - Main menu");

                    if (ButtonsNameConstants.CREATE_ISSUE.equals(update.getMessage().getText())) {
                        storeUser.get(userChatId).updateStep(Steps.STEP120);
                        executeMessage(sendMessageBotService.chooseTypeIssueMessage(userChatId));
                    }

                    if (ButtonsNameConstants.LIST_ISSUES.equals(update.getMessage().getText())) {
                        storeUser.get(userChatId).updateStep(Steps.STEP110);

                    }

                //Список заявок
                case STEP110:

                    log.info("[" + getClass() + "] " + userChatId + " STEP110 - List orders");

                    if (storeUser.get(userChatId).getStep().equals(Steps.STEP110)) {
                        executeMessage(sendMessageBotService.awaitingMessage(userChatId));
                        List<JiraIssueURL> j = jiraApi.listIssues(storeUser.get(userChatId).getUserName());
                        String listIssues = generate(j);

                        executeMessage(sendMessageBotService.listOfIssues(userChatId, listIssues));

                        executeMessage(sendMessageBotService.listOfIssuesEnd(userChatId));
                        storeUser.get(userChatId).updateStep(Steps.STEP999);
                    }

                    break;


                // Создание заявки. Выбор проекта
                case STEP120:
                    log.info("[" + getClass() + "] " + userChatId + " STEP120 - Project choose");

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

                    storeOrders.save(userChatId, new Issue(project, storeUser.get(userChatId).getUserName()));
                    storeOrders.get(userChatId).setIssueType(issueType);

                    storeUser.get(userChatId).updateStep(Steps.STEP121);
                    executeMessage(sendMessageBotService.writeDivisionIssue(userChatId));

                    break;


                //Создание заявки. Выбор подразделения
                case STEP121:
                    log.info("[" + getClass() + "] " + userChatId + " STEP121 - Department choose");

                    //CheckDivision checkDivision = new CheckDivision();
                    executeMessage((sendMessageBotService.awaitingMessage(userChatId)));

                    if (userEnteredText.length() == DIVISION_NUMBER_LENGTH
                            && isDivisionReal(userEnteredText)) {
                        storeUser.get(userChatId).updateStep(Steps.STEP122);
                        storeOrders.get(userChatId).setDepartment(userEnteredText);
                        executeMessage(sendMessageBotService.writeNameIssue(userChatId));
                    } else {
                        executeMessage(sendMessageBotService.errorDivisionIssue(userChatId));
                    }

                    break;

                // Создание заявки. Название заявки
                case STEP122:
                    log.info("[" + getClass() + "] " + userChatId + " STEP122 - Name issue");

                    try {
                        storeOrders.get(userChatId).setNameIssue(userEnteredText);
                        storeUser.get(userChatId).updateStep(Steps.STEP123);
                        executeMessage(sendMessageBotService.writeDescriptionIssue(userChatId));
                    } catch (JiraApiException e) {
                        executeMessage(sendMessageBotService.writeNameIssueError(userChatId));
                    }

                    break;

                // Создание заявки. Описание
                case STEP123:
                    log.info("[" + getClass() + "] " + userChatId + " STEP123 - Description");

                    try {
                        storeOrders.get(userChatId).setDescription(userEnteredText);
                        storeUser.get(userChatId).updateStep(Steps.STEP124);
                        executeMessage(sendMessageBotService.messageUserContacts(userChatId));
                    } catch (JiraApiException e) {
                        executeMessage(sendMessageBotService.writeDescriptionIssueError(userChatId));
                    }

                    break;

                // Создание заявки. Контактные данные
                case STEP124:
                    log.info("[" + getClass() + "] " + userChatId + " STEP124 - Contact");
                    try {
                        storeOrders.get(userChatId).setContact(userEnteredText);

                        if (Objects.equals(storeOrders.get(userChatId).getIssueType(), JiraConstants.JIRA_ISSUE_TYPE_REGULAR)) {
                            storeUser.get(userChatId).updateStep(Steps.STEP125);
                            executeMessage(sendMessageBotService.messageEnterAnyDeskID(userChatId));
                        } else {
                            storeUser.get(userChatId).updateStep(Steps.STEP126);
                            executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                        }
                    } catch (JiraApiException e) {
                        executeMessage(sendMessageBotService.messageUserContactsError(userChatId));
                    }

                    break;

                // Создание заявки. AnyDesk
                case STEP125:
                    log.info("[" + getClass() + "] " + userChatId + " STEP125 - AD");
                    try {
                        storeOrders.get(userChatId).setIdanydesk(userEnteredText);
                        storeUser.get(userChatId).updateStep(Steps.STEP126);
                        executeMessage(sendMessageBotService.messageAddAttachments(userChatId));
                    } catch (JiraApiException e){
                        executeMessage(sendMessageBotService.messageWrongAnyDeskID(userChatId));

                    }

                    break;

                // Создание заявки. Прикрепление доп.файлов
                case STEP126:
                    log.info("[" + getClass() + "] " + userChatId + " STEP126 - Attachment");

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
                        //String ext = FilenameUtils.getExtension(String.valueOf(fileTG.getFilePath()));
                        String[] str = String.valueOf(fileTG.getFilePath()).split("/");
                        String fileName = Arrays.stream(str).skip(str.length - 1).findFirst().orElse("untitled");

                        log.info("[" + getClass() + "] " + userChatId + " STEP126 - add file: " + fileName);

                        File file = downloadFile(fileTG, new File(
                                Config.getPathToExchange()
                                + userChatId
                                + "_" + fileName)); //+ "." + ext));

                        storeOrders.get(userChatId).addAttachmentFile(file.getPath());

                    } catch (TelegramApiException e) {
                        log.error("[" + getClass() + "] " + userChatId + " STEP126 - Attachment " + e.getMessage());
                    }

                    break;

                //Создание заявки. Проверка введенных данных
                case STEP127:
                    log.info("[" + getClass() + "] " + userChatId + " STEP127 - Creating issue in Jira...");

                    try {
                        String key = CreateJiraIssue.createJiraIssue(jiraApi, storeOrders.get(userChatId));

                        if (storeOrders.get(userChatId).getAttachment() != null) {
                            jiraApi.addMultiAttachment(key, storeOrders.get(userChatId).getAttachment());
                        }

                        log.info("[" + getClass() + "] " + userChatId + " STEP127 -  Issue: " + storeOrders.get(userChatId).toString());
                        executeMessage(sendMessageBotService.messageEndCreatingIssue(userChatId, getUrl(key)));

                    } catch (JiraApiException e) {
                        storeUser.get(userChatId).updateStep(Steps.STEP997);
                        executeMessage(sendMessageBotService.messageCreatingIssueError(userChatId));
                    }


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
                    log.info("[" + getClass() + "] " + userChatId + " STEP998 - Deleting issue params in map...");
                    log.info("[" + getClass() + "] " + userChatId + " STEP998 -  Issue: " + storeOrders.contains(userChatId));

                // Возврат в меню
                case STEP999:
                    log.info("[" + getClass() + "] " + userChatId + " STEP999 -  end action ");
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
