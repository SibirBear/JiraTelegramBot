package info.fermercentr.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/*
 * Класс чтения и установки параметров конфигурации
 *
 * Class for read & execute configuration
 */

public class Config {

    public static final String APP_NAME = "JiraTelegramBot";
    private static final String CONFIG_PATH = "projects-configs/";
    private static final String LOGS_PATH = "projects-logs/";

    private static ConfigTelegramSettings configTelegramSettings;
    private static ConfigOracleDB configOracleDB;
    private static Config config;
    private static String groupId;
    private static String authJira;
    private static String urlJira;
    private static String pathToExchange;
    private static int daysForReAuth;
    private static String helpVideoURL;

    private static Config read(Properties properties) {

        try(InputStream is = Files.newInputStream(Paths.get(CONFIG_PATH + APP_NAME + "/config.properties"))) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String token = properties.getProperty("TOKEN");
        String botName = properties.getProperty("BOTNAME");
        groupId = properties.getProperty("GROUP");
        authJira = properties.getProperty("AUTH_JIRA");
        urlJira = properties.getProperty("URL_JIRA");
        pathToExchange = properties.getProperty("PATH_TO_EXCHANGE");
        String dbOracleUrl = properties.getProperty("DB_ORACLE_URL");
        String dbOracleUser = properties.getProperty("DB_ORACLE_USER");
        String dbOraclePass = properties.getProperty("DB_ORACLE_PASS");
        daysForReAuth = Integer.parseInt(properties.getProperty("DAYS_FOR_RE_AUTH"));
        helpVideoURL = properties.getProperty("HELP_VIDEO_URL");

        configTelegramSettings = new ConfigTelegramSettings(token, botName);
        configOracleDB = new ConfigOracleDB(dbOracleUrl, dbOracleUser,dbOraclePass);

        return config;
    }

    public static Config init() {
        Properties properties = new Properties();
        config = read(properties);
        return config;
    }

    public static ConfigTelegramSettings getConfigTelegramSettings() {
        return configTelegramSettings;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static String getAuthJira() {
        return authJira;
    }

    public static String getUrlJira() {
        return urlJira;
    }

    public static String getPathToExchange() {
        return LOGS_PATH + APP_NAME + pathToExchange;
    }

    public static int getDaysForReAuth() {
        return daysForReAuth;
    }

    public static ConfigOracleDB getConfigOracleDB() {
        return configOracleDB;
    }

    public static String getHelpVideoURL() {
        return helpVideoURL;
    }

}
