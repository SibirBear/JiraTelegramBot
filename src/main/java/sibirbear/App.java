package sibirbear;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sibirbear.config.Config;
import sibirbear.core.CoreBot;

/*
 * Основной класс запуска
 */
public class App {

    //private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        Config.init();
        System.out.println("start");

        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new CoreBot());
            System.out.println("start2");
        } catch (TelegramApiException e) {
            //log.error("Error starting app. " + e.getStackTrace());
            System.out.println("ERROR: " + e.getMessage());
        }

    }
}
