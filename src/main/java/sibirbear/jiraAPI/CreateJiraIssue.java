package sibirbear.jiraAPI;

import sibirbear.jiraAPI.exceptions.JiraApiException;
import sibirbear.jiraAPI.issue.Issue;
import sibirbear.jiraAPI.issue.IssueBuilder;


public class CreateJiraIssue {

    public static String createJiraIssue(final JiraAPI api, final Issue issue) throws JiraApiException {
        if (api == null) throw new JiraApiException("Cannot create Jira issue, parameter api is null.");

        String query = new IssueBuilder(issue).build();
        String key = api.httpCreateIssue(query);
        issue.setCreated(true);

        return key;
    }

}
