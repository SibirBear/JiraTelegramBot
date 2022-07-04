package info.fermercentr.jiraAPI.issue;

import info.fermercentr.jiraAPI.JiraConstants;
import info.fermercentr.jiraAPI.exceptions.JiraApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssueBuilderTest {

    final Issue issueTest = new Issue("project", "reporter");
    final String expected = "{\"fields\":{\"summary\":\"name\",\"issuetype\":{\"id\":\"29\"},\"project\":{\"id\":\"project\"},\"description\":\"Test\\nAnydesk: 123456789\",\"reporter\":{\"name\":\"reporter\"},\"customfield_10231\":\"1234\",\"customfield_10420\":\"contact\"}}";

    IssueBuilderTest() throws JiraApiException {
    }

    @Test
    void buildTestOne() throws JiraApiException {
        issueTest.setIssueType(JiraConstants.JIRA_ISSUE_TYPE_REGULAR);
        issueTest.setNameIssue("name");
        issueTest.setContact("contact");
        issueTest.setDepartment("1234");
        issueTest.setDescription("Test");
        issueTest.setIdanydesk("123456789");

        String actual = new IssueBuilder(issueTest).build();

        Assertions.assertEquals(expected, actual);

    }

    @Test
    void exceptionTestingIssueName() {
        Exception exception = assertThrows(JiraApiException.class, () ->
                issueTest.setNameIssue("name\uD83E\uDD33"));
        assertEquals("Issue name cannot be Null or empty", exception.getMessage());
    }

    @Test
    void exceptionTestingDescription() {
        Exception exception = assertThrows(JiraApiException.class, () ->
                issueTest.setDescription("description\uD83E\uDD33"));
        assertEquals("Issue description cannot be Null or empty", exception.getMessage());
    }

    @Test
    void exceptionTestingContact() {
        Exception exception = assertThrows(JiraApiException.class, () ->
                issueTest.setContact("contact\uD83E\uDD33"));
        assertEquals("Contact of Issue author cannot be Null or empty", exception.getMessage());
    }


}