package sibirbear;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sibirbear.config.Config;
import sibirbear.core.CoreBot;
import sibirbear.tasks.InitTasks;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static sibirbear.service.CheckDivision.setDivisionsFromDB;

/*
 * Основной класс запуска
 */
@Startup
@Singleton(name="Telegram-bot-for-creating-Jira-issues")
public class App {

    private static final Logger log = LogManager.getLogger(App.class);
    private BotSession botSession;
    private CoreBot coreBot;
    private InitTasks initTasks;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture;
    private final int SCHEDULE_TIME_TO_INIT = 12;
    private final int SCHEDULE_TIME_TO_DELAY = 6;

    // TODO: на старте проверять папку TempImage (если создана) и удалять оттуда файлы
    //
//    public static void main(String[] args) {

    @PostConstruct
    public void init() {
        Config.init();
        log.info("[" + Config.APP_NAME + "] - Configuration properties are initialized.");
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

       // TODO: сделать выполнение задачи на старте и по расписанию раз в день
        setDivisionsFromDB();

        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            coreBot = new CoreBot();
            botSession = botsApi.registerBot(coreBot);
            log.info("[" + Config.APP_NAME + "] - Bot starting...");

            System.out.println("Bot starting...");

            initTasks = new InitTasks();
            executeRepeatingTask();

        } catch (TelegramApiException e) {
            log.error("Error starting app. " + e.getMessage());
            System.out.println("ERROR: " + e.getMessage());
        }

    }

    private void executeRepeatingTask() {
        log.info("[" + Config.APP_NAME + "] - Init scheduled tasks.");

        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                () ->
                {
                    initTasks.start(
                            coreBot.getStoreUsers(),
                            coreBot.getStoreOrders()
                    );

                }, SCHEDULE_TIME_TO_INIT, SCHEDULE_TIME_TO_DELAY, TimeUnit.HOURS
        );
    }

    @PreDestroy
    public void destroy() {
        log.info("[" + Config.APP_NAME + "] - Stop App. Begin stop process...");

        try {
            log.info("[" + Config.APP_NAME + "] - Stop App. Trying to stop scheduled tasks...");
            scheduledFuture.cancel(false);
        } catch (Exception e) {
            log.error("[" + Config.APP_NAME + "] - Stop App. ERROR while trying stop tasks! "
                    + e.getMessage());
        }

        boolean wait = true;
        while(wait) {
            try {
                if (scheduledFuture.isDone()) wait = false;
            } catch (Exception e) {
                log.error("[" + Config.APP_NAME + "] - Stop App. ERROR while trying stop tasks! (isDone). "
                        + e.getMessage());
                wait = false;
            }
        }

        try {
            scheduledExecutorService.shutdown();
            log.info("[" + Config.APP_NAME + "] - Stop App. Successfully stopped.");
        } catch (Exception e) {
            log.error("[" + Config.APP_NAME + "] - Stop App. ERROR when trying to stop scheduleExecutorService. "
                    + e.getMessage());
        }



        if (botSession.isRunning()) {
            botSession.stop();
        }
    }
}
