package sibirbear.service;

import sibirbear.core.CoreConstants;
import sibirbear.model.Steps;
import sibirbear.model.User;

import java.time.LocalDate;

//класс для проверки соблюдения условий для хранения записи User
public class CheckUserAuth {

    private final User user;

    public CheckUserAuth(User user) {
        this.user = user;
    }

    public boolean check() {
        return checkDateIsExpired()
                || checkNotAuth();
    }

    //Проверяем, что дата авторизации просрочена и пользователь не создает заявку (шаг 101 - главное меню)
    private boolean checkDateIsExpired() {
        return user.isDateExpired()
                && Steps.STEP101.equals(user.getStep());
    }

    //Проверяем, что дата добавления записи старше 1 дня и пользователь не авторизован
    private boolean checkNotAuth() {
        return user.getDateAuth().isBefore(LocalDate.now())
                && CoreConstants.NOT_AUTH.equals(user.getUserName());
    }

}
