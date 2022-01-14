package sibirbear.service;

public enum SendMessageConstantText {

    GREETINGS("Доброго времени суток!\n\nЯ чат-бот ФЦ Франчайзинг.\nЯ помогу тебе разместить заявку по Вашей проблеме в Jira."),
    AUTH("Для начала тебе необходимо авторизоваться.\nНапиши свой логин для создания заявок.");

    private final String text;

    SendMessageConstantText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
