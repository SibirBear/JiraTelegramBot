package sibirbear.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import sibirbear.config.Config;


/*
 * Класс ядра бота, основная логика
 */

public class CoreBot extends TelegramLongPollingBot {

    private final String token = Config.getConfigTelegramSettings().getToken();
    private final String botName = Config.getConfigTelegramSettings().getBotName();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            //дописать
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
