package info.fermercentr.service.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import static java.lang.Math.toIntExact;

/*
 * Конструкторы сообщений
 */

public class SendMessageBuilder {

    //Простое сообщение
    public static SendMessage createSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        return sendMessage;
    }

    //Простое сообщение с удалением клавиатуры
    public static SendMessage createSimpleMessageDeleteKeyboard(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    //Простое сообщение с меню
    public static SendMessage createMessageWithKeyboard(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = createSimpleMessage(chatId, text);
        message.setReplyMarkup(keyboard);
        return  message;
    }

    //Простое сообщение со встроенными кнопками
    public static SendMessage createMessageWithKeyboard(long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = createSimpleMessage(chatId, text);
        message.setReplyMarkup(keyboard);
        return  message;
    }

    //Сообщение для замещения предыдущего сообщения
    public static EditMessageText editSimpleMessage(Update update, String text) {
        EditMessageText message = new EditMessageText();
        message.setMessageId(toIntExact(update.getCallbackQuery().getMessage().getMessageId()));
        message.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        message.setText(text);
        return message;
    }

    //Сообщение со встроенными кнопками для замещения предыдущего сообщения
    public static EditMessageText editMessageWithKeyboard(
            Update update, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText message = editSimpleMessage(update, text);
        message.setReplyMarkup(keyboard);
        return message;
    }
}
