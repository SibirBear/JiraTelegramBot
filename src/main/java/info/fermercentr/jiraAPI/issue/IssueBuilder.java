package info.fermercentr.jiraAPI.issue;

import info.fermercentr.jiraAPI.JiraConstants;
import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import org.json.JSONObject;

import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_DESCRIPTION;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_FIELDS;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_ID;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_ISSUETYPE;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_NAME;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_PROJECT;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_REPORTER;
import static info.fermercentr.jiraAPI.issue.IssueConstants.KEY_SUMMARY;

public class IssueBuilder {

    private final Issue issue;

    public IssueBuilder(Issue issue) {
        this.issue = issue;
    }

    public String build() {
        if (!isValid()) {
            throw new JiraApiException("Cannot build issue query: NPE or saved issue data is already used.");
        }

        JSONObject project = new JSONObject();
        project.put(KEY_ID, issue.getProject());

        JSONObject issueType = new JSONObject();
        issueType.put(KEY_ID, issue.getIssueType());

        JSONObject reporter = new JSONObject();
        reporter.put(KEY_NAME, issue.getReporter());

        JSONObject fields = new JSONObject();
        fields.put(KEY_PROJECT, project);
        fields.put(KEY_SUMMARY, issue.getNameIssue());

        String description = issue.getDescription();
        if (issue.getIdAnydesk() != null) {
            description = description + "\nAnydesk: " + issue.getIdAnydesk();
        }
        fields.put(KEY_DESCRIPTION, description);

        fields.put(KEY_ISSUETYPE, issueType);
        fields.put(KEY_REPORTER, reporter);
        fields.put(JiraConstants.JIRA_FIELD_CUSTOM_CONTACT, issue.getContact());
        fields.put(JiraConstants.JIRA_FIELD_CUSTOM_DIVISION, issue.getDepartment());

        JSONObject newIssueJsonQuery = new JSONObject();
        newIssueJsonQuery.put(KEY_FIELDS, fields);

        return newIssueJsonQuery.toString();
    }

    private boolean isValid() {
        return isStringNullOrEmpty(issue.getProject())
                || isStringNullOrEmpty(issue.getIssueType())
                || isStringNullOrEmpty(issue.getReporter())
                || isStringNullOrEmpty(issue.getNameIssue())
                || isStringNullOrEmpty(issue.getDescription())
                || isStringNullOrEmpty(issue.getContact())
                || isStringNullOrEmpty(issue.getDepartment())
                || !issue.isCreated();
    }

    private boolean isStringNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

}
