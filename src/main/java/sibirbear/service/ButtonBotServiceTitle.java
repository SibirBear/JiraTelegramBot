package sibirbear.service;

public enum ButtonBotServiceTitle {

    YES("✅");

    private final String title;

    ButtonBotServiceTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
