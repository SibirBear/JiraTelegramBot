package info.fermercentr.jiraAPI.issue;

import info.fermercentr.jiraAPI.JiraConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IssueBuilderTest {

    final Issue issueTest = new Issue("project", "reporter");
    final String expected = "{\"fields\":{\"summary\":\"name\",\"issuetype\":{\"id\":\"29\"},\"project\":{\"id\":\"project\"},\"description\":\"Test\\nAnydesk: 123456789\",\"reporter\":{\"name\":\"reporter\"},\"customfield_10231\":\"1234\",\"customfield_10420\":\"contact\"}}";

    @Test
    void buildTest() {
        issueTest.setIssueType(JiraConstants.JIRA_ISSUE_TYPE_REGULAR);
        issueTest.setNameIssue("name");
        issueTest.setContact("contact");
        issueTest.setDepartment("1234");
        issueTest.setDescription("Test");
        issueTest.setIdanydesk("123456789");

        String actual = new IssueBuilder(issueTest).build();

        Assertions.assertEquals(expected, actual);

    }


}