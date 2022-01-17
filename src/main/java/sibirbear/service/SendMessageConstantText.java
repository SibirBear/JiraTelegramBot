package sibirbear.service;

public enum SendMessageConstantText {

    GREETINGS("Доброго времени суток!\n\nЯ чат-бот ФЦ Франчайзинг.\nЯ помогу тебе разместить заявку по Вашей проблеме в Jira1."),
    AUTH("Для начала тебе необходимо авторизоваться.\nНапиши свой логин для создания заявок."),
    AUTH_OK("✅ Авторизация прошла успешно!"),
    AUTH_ERROR("Такого пользователя не существует.\nПопробуйте еще раз."),
    DESIRECREATEREQUEST("Хотите создать заявку?\nЕсли да - нажмите галочку");

    private final String text;

    SendMessageConstantText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
