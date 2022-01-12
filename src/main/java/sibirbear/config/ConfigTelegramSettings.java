package sibirbear.config;

public class ConfigTelegramSettings {

    private String token;
    private String botName;

    public ConfigTelegramSettings(String token, String botName) {
        this.token = token;
        this.botName = botName;
    }

    public String getToken() {
        return token;
    }

    public String getBotName() {
        return botName;
    }
}
