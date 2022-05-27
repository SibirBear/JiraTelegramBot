package info.fermercentr.model;

import info.fermercentr.config.Config;

import java.time.LocalDate;
import java.util.Objects;

/*
 * Модель для хранения пользователя Jira с датой авторизации и номером следующего не завершенного шага.
 * На основании сохраненного шага при получении сообщения выполняется соответствующий метод.
 * На основании даты авторизации данный Пользователь будет удален из таблицы для повторной авторизации.
 *
 * A model for storing a Jira user with the authorization date and the number of the next incomplete step.
 * Based on the saved step, the corresponding method is executed when the message is received.
 * Based on the authorization date, this User will be removed from the table for re-authorization.
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
        this.expiredDate = date.plusDays(Config.getDaysForReAuth());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Steps getStep() {
        return step;
    }

    public void updateStep(Steps step) {
        this.step = step;
    }

    public LocalDate getDateAuth() {
        return date;
    }

    public boolean isDateExpired() {
        return LocalDate.now().isAfter(expiredDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userName.equals(user.userName)
                && Objects.equals(date, user.date)
                && step == user.step
                && Objects.equals(expiredDate, user.expiredDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, date, step, expiredDate);
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", date=" + date +
                ", step=" + step +
                ", expiredDate=" + expiredDate +
                '}' + "\n";
    }
}
