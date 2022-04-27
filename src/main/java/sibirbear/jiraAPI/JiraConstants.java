package sibirbear.jiraAPI;

public class JiraConstants {

//Options
//какое количество знаков из описания заявки сохранить в объекте JiraIssueURL
    public static final String JIRA_ISSUE_URL_BROWSE = "https://jira.fermer-centr.shop/browse/";
    public static final int JIRA_ISSUE_URL_DESCRIPTION_LENGTH = 30;

//Константа имени скачанного файла из бота для прикрепления к заявке
    public static final String JIRA_NAME_ATTACH_FILE = "Attach_from_bot _";

//Projects (PROJECT_)
    public static final String JIRA_PROJECT_FRANCH = "18990";
    public static final String JIRA_PROJECT_SUPPORT = "12390";

//Issue types (ISSUE_TYPE_)
    public static final String JIRA_ISSUE_TYPE_REGULAR = "29";
    public static final String JIRA_ISSUE_TYPE_CREATE_GOODS = "11201";
    public static final String JIRA_ISSUE_TYPE_REPAIR = "11300";

//Custom fields (FIELD_CUSTOM_)
    public static final String JIRA_FIELD_CUSTOM_CONTACT = "customfield_10420";  // соответствует полю "Контактное лицо"
    public static final String JIRA_FIELD_CUSTOM_DIVISION = "customfield_10231"; // соответствует полю "Подразделение"

}
