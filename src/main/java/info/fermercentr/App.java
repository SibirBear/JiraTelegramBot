package info.fermercentr;

import info.fermercentr.config.Config;
import info.fermercentr.core.CoreBot;
import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import info.fermercentr.service.CheckDivision;
import info.fermercentr.tasks.InitTasks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/*
 * Основной класс запуска
 *
 * Main class
 */
@Startup
@Singleton(name = "Telegram-bot-Jira-Issues")
public class App {

    public static final String BUILD_VERSION = "04/08/2022 16-30";
    private static final Logger LOG = LogManager.getLogger(App.class);
    private BotSession botSession;
    private CoreBot coreBot;
    private InitTasks initTasks;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture;

    private final int SCHEDULE_TIME_TO_INIT = 0;
    private final int SCHEDULE_TIME_TO_DELAY = 6;

    @PostConstruct
    public void init() {
        LOG.info("[" + Config.APP_NAME + "] - Starting application (build " + BUILD_VERSION + ")...");
        LOG.info("[" + Config.APP_NAME + "] - Trying to init Configuration properties...");
        Config.init();
        LOG.info("[" + Config.APP_NAME + "] - Configuration properties are initialized.");

        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        CheckDivision.setDivisionsFromDB();
        LOG.info("[" + Config.APP_NAME + "] - List of Divisions received.");

        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            coreBot = new CoreBot();
            botSession = botsApi.registerBot(coreBot);
            LOG.info("[" + Config.APP_NAME + "] - Bot starting...");

            initTasks = new InitTasks();
            executeRepeatingTask();

        } catch (TelegramApiException | JiraApiException e) {
            LOG.error("Error starting app. " + e.getMessage(), e);
        }

    }

    private void executeRepeatingTask() {
        LOG.info("[" + Config.APP_NAME + "] - Init scheduled tasks...");

        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                () ->
                        initTasks.start(
                                coreBot.getStoreUsers(),
                                coreBot.getStoreOrders()
                        ), SCHEDULE_TIME_TO_INIT, SCHEDULE_TIME_TO_DELAY, TimeUnit.HOURS
        );
    }

    @PreDestroy
    public void stop() {
        LOG.info("[" + Config.APP_NAME + "] - Stop App. Begin stop process...");

        try {
            LOG.info("[" + Config.APP_NAME + "] - Stop App. Trying to stop scheduled tasks...");
            scheduledFuture.cancel(false);
        } catch (Exception e) {
            LOG.error("[" + Config.APP_NAME + "] - Stop App. ERROR while trying stop tasks! "
                    + e.getMessage());
        }

        boolean wait = true;
        while(wait) {
            try {
                if (scheduledFuture.isDone()) wait = false;
            } catch (Exception e) {
                LOG.error("[" + Config.APP_NAME + "] - Stop App. ERROR while trying stop tasks! (isDone). "
                        + e.getMessage());
                wait = false;
            }
        }

        try {
            scheduledExecutorService.shutdown();
            LOG.info("[" + Config.APP_NAME + "] - Stop App. Successfully stopped.");
            LOG.info("[" + Config.APP_NAME + "] ***** End *****");
        } catch (Exception e) {
            LOG.error("[" + Config.APP_NAME + "] - Stop App. ERROR when trying to stop scheduleExecutorService. "
                    + e.getMessage());
        }

        if (botSession.isRunning()) {
            botSession.stop();
        }
    }
}
