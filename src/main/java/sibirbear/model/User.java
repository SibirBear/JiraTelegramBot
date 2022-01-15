package sibirbear.model;

import java.time.LocalDate;

/*
 * Модель для хранения пользователя Jira с датой авторизации и номером следующего не завершенного шага.
 * На основавнии сохраненного шага при получения сообщения выполняется соответствующий метод.
 * На основании даты авторизации данный Пользователь будет удален из таблицы для повторной авторизации.
 */

public class User {

    private long userName;
    private LocalDate date;
    private Steps step;

    public User(final long userName, final LocalDate date, final Steps step) {
        this.userName = userName;
        this.date = date;
        this.step = step;
    }

    public long getUserName() {
        return userName;
    }

    public LocalDate getDate() {
        return date;
    }

    public Steps getStep() {
        return step;
    }

    public void updateDate(LocalDate localDate) {
        this.date = localDate;
    }

    public void updateStep(Steps step) {
        this.step = step;
    }

}
