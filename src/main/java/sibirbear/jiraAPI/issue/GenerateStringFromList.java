package sibirbear.jiraAPI.issue;

import sibirbear.jiraAPI.JiraIssueURL;

import java.util.List;

public class GenerateStringFromList {

    public static String generate(final List<JiraIssueURL> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Список заявок:*\n\n");

        if (list.size() == 0) {
            sb.append("_Открытых заявок, созданных тобой - нет._\n");
        } else {
            for (JiraIssueURL jiraIssueURL : list) {
                String url = "*" + jiraIssueURL.getUrl() + "*\n";
                String description = "_" + jiraIssueURL.getDescription() + "_\n\n";
                sb.append(url);
                sb.append(description);
            }
        }

        return sb.toString();
    }

}
