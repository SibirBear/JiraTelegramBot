package info.fermercentr.config;

public class ConfigOracleDB {

    private String url;
    private String user;
    private String pass;

    public ConfigOracleDB(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
