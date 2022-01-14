package sibirbear.model;

import java.time.LocalDate;

/*
 * Модель для хранения пользователя Jira с датой авторизации, чтобы в дальнейшем очищать
 * таблицу если дата авторизации истекла (в соответсвии с установленными правилами)
 */

public class User {

    private String userName;
    private LocalDate date;

    public User(final String userName, final LocalDate date) {
        this.userName = userName;
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDate getDate() {
        return date;
    }

}
