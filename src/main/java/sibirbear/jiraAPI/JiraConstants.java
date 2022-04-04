package sibirbear.jiraAPI;

public class JiraConstants {

//Options
//какое количество знаков из описания заявки сохранить в объекте JiraIssueURL
    public static final String JIRA_ISSUE_URL_BROWSE = "https://jira.fermer-centr.shop/browse/";
    public static final int JIRA_ISSUE_URL_DESCRIPTION_LENGTH = 30;

//Projects (PROJECT_)
    public static final String PROJECT_FRANCH = "18990";
    public static final String PROJECT_SUPPORT = "12390";

//Issue types (ISSUE_TYPE_)
    public static final String ISSUE_TYPE_REGULAR = "29";
    public static final String ISSUE_TYPE_CREATE_GOODS = "11201";
    public static final String ISSUE_TYPE_REPAIR = "11300";

//Custom fields (FIELD_CUSTOM_)
    public static final String FIELD_CUSTOM_CONTACT = "customfield_10420";  // соответствует полю "Контактное лицо"
    public static final String FIELD_CUSTOM_DIVISION = "customfield_10231"; // соответствует полю "Подразделение"

}
