package sibirbear.service;

import org.json.JSONObject;
import sibirbear.jiraAPI.JiraAPI;
import sibirbear.model.Order;

import java.io.IOException;

public class CreateOrderJira {
    // TODO: Добавить добавление сохраненных файлов
    public static void createJiraIssue(JiraAPI api, Order order) {

        JSONObject project = new JSONObject();
        project.put("id", order.getProject());

        JSONObject issueType = new JSONObject();
        issueType.put("id", order.getIssueType());

        JSONObject reporter = new JSONObject();
        reporter.put("name", order.getReporter());

        JSONObject fields = new JSONObject();
        fields.put("project", project);
        fields.put("summary", order.getNameIssue());
        fields.put("description", order.getDescription() + "\nAnydesk: " + order.getIdanydesk());
        fields.put("issuetype", issueType);
        fields.put("reporter", reporter);
        fields.put("customfield_10420", order.getContact());
        fields.put("customfield_10231", order.getDepartment());

        JSONObject newIssue = new JSONObject();
        newIssue.put("fields", fields);

        try {
                    System.out.println("JSON:");
                    System.out.println(newIssue.toString());

                    String s = newIssue.toString();
                    System.out.println("String:\n" + s);

            String key = api.httpCreateIssue(newIssue.toString());

                    System.out.println(key);

            order.setCreated(true);
        } catch (IOException e) {
            e.printStackTrace();
                    System.out.println(e.getMessage());
        }

    }
    /*    "{ fields: " +
            "{ " +
            "project: { id: 18990 }, " +
            "summary: Test issue, " +
            "description: Test message, " +
            "issuetype: { id: 29}, " +
            "customfield_10420: Test_Bot, " +
            "customfield_10231: 5056" +
            "} " +
    "}"*/

    /*
        JSONObject j = new JSONObject();
        JSONObject i = new JSONObject();
        i.put("1", "1");
        j.put("2", i);
        System.out.println(j);
*/

}
