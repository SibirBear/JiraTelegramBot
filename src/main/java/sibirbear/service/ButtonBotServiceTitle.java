package sibirbear.service;

public enum ButtonBotServiceTitle {

    YES("✅"),
    EXECUTIVEDIRECTION("Проблема с торговым оборудованием, помещением, коммуникациями."),
    ITSUPPORT("Проблема с компьютером и периферийным оборудованием"),
    GOODS("Создание номенклатуры или изменение ее параметров"),
    ONEC("Проблема с 1С или нужна помощь по 1С");

    private final String title;

    ButtonBotServiceTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
