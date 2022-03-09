package sibirbear;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sibirbear.config.Config;
import sibirbear.core.CoreBot;

import java.util.Arrays;

/*
 * Основной класс запуска
 */
public class App {

    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        Config.init();
        log.info("[" + Config.APP_NAME + "] - Configuration properties are initialized.");

        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new CoreBot());
            log.info("[" + Config.APP_NAME + "] - Bot starting...");

            System.out.println("Bot starting...");

        } catch (TelegramApiException e) {
            log.error("Error starting app. " + e.getMessage());

            System.out.println("ERROR: " + e.getMessage());
        }

    }
}
