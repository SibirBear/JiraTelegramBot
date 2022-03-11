package sibirbear.model;

import java.time.LocalDate;

/*
 * Модель для хранения пользователя Jira с датой авторизации и номером следующего не завершенного шага.
 * На основании сохраненного шага при получении сообщения выполняется соответствующий метод.
 * На основании даты авторизации данный Пользователь будет удален из таблицы для повторной авторизации.
 */

public class User {

    private String userName;
    private final LocalDate date;
    private Steps step;
    private final LocalDate expiredDate;

    public User(final String userName, final Steps step) {
        this.userName = userName;
        this.date = LocalDate.now();
        this.step = step;
        this.expiredDate = date.minusDays(8);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getDate() {
        return date;
    }

    public Steps getStep() {
        return step;
    }

    public void updateStep(Steps step) {
        this.step = step;
    }

    public boolean isDateExpired() {
        return LocalDate.now().isBefore(expiredDate);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", date=" + date +
                ", step=" + step +
                ", expiredDate=" + expiredDate +
                '}';
    }
}
