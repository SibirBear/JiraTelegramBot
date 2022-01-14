package sibirbear.service;

public enum SendMessageConstantText {

    GREETINGS("Доброго времени суток!\nЯ чат-бот ФЦ Франчайзинг.\nЯ помогу Вам разместить заявку по Вашей проблеме в Jira.");

    private final String text;

    SendMessageConstantText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
