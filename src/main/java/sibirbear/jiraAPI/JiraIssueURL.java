package sibirbear.jiraAPI;

public class JiraIssueURL {

    private final String url;
    private final String description;

    /**
     * Класс для хранения ссылки на заявку в Jira и определенное в константе JIRA_ISSUE_URL_DESCRIPTION_LENGTH
     * количество знаков из описания заявки для вывода списка открытых заявок.
     * @param key ключ заявки вида ПРОЕКТ-НОМЕР ЗАЯВКИ (TEST-1234)
     * @param description описание заявки
     */
    public JiraIssueURL(final String key, final String description) {
        this.url = JiraConstants.JIRA_ISSUE_URL_BROWSE + key;

        if (description == null) {
            this.description = "";
        }
        else if (description.length() > JiraConstants.JIRA_ISSUE_URL_DESCRIPTION_LENGTH) {
            this.description = description.substring(0, JiraConstants.JIRA_ISSUE_URL_DESCRIPTION_LENGTH) + "...";
        }
        else {
            this.description = description;
        }

    }

    public String getUrl() {
        return url;
    }

    /**
     * Метод, который возвращает URL ссылку на заявку по переданному ключу.
     * К ключу будет добавлен адрес, по которому размещен сервер Jira
     * @param key ключ заявки вида ПРОЕКТ-НОМЕР ЗАЯВКИ (TEST-1234)
     * @return возвращает ссылку на заявку в виде String
     */
    public static String getUrl(final String key) {
        return JiraConstants.JIRA_ISSUE_URL_BROWSE + key;
    }

    public String getDescription() {
        return description;
    }
}
