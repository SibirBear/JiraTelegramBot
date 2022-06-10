package info.fermercentr.jiraAPI;

import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import info.fermercentr.jiraAPI.issue.Issue;
import info.fermercentr.jiraAPI.issue.IssueBuilder;


public class CreateJiraIssue {

    public static String createJiraIssue(final JiraAPI api, final Issue issue) throws JiraApiException {
        if (api == null || issue == null) throw new JiraApiException("Cannot create Jira issue, parameter api or issue is null.");

        String query = new IssueBuilder(issue).build();
        String key = api.httpCreateIssue(query);
        issue.setCreated(true);

        return key;
    }

}
