package sibirbear;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sibirbear.config.Config;
import sibirbear.core.CoreBot;

import java.util.Arrays;

import static sibirbear.service.CheckDivision.setDivisionsFromDB;

/*
 * Основной класс запуска
 */
public class App {

    private static final Logger log = LogManager.getLogger(App.class);

    // TODO: на старте проверять папку TempImage (если создана) и удалять оттуда файлы
    //
    // TODO: сделать класс для удаления мусора (папка TempImage, User, Order) который через определенный
    //  интервал удаляет не актуальные данные или при завершении программы. Не актуальные данные:
    //  1. Данные по заявке не используются более 12 часов - удаляем данные
    //  2. Юзер авторизован более установленного времени - удаление юзера, его данных по заявке
    //  3. Удаляем файлы в TempImage если на них нет ссылки в Orders

    public static void main(String[] args) {
        Config.init();
        log.info("[" + Config.APP_NAME + "] - Configuration properties are initialized.");

       // TODO: сделать выполнение задачи на старте и по расписанию раз в день
        setDivisionsFromDB();

        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            BotSession botSession = botsApi.registerBot(new CoreBot());

            /* //При завершении
            botSession.stop();
            */

            log.info("[" + Config.APP_NAME + "] - Bot starting...");

            System.out.println("Bot starting...");

        } catch (TelegramApiException e) {
            log.error("Error starting app. " + e.getMessage());

            System.out.println("ERROR: " + e.getMessage());
        }

    }
}
